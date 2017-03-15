package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.async.Action;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.utils.SlackUtils;

public abstract class MessageItemData extends ItemData<ItemData.ViewHolder> {

    @Nullable
    private SlackUser sender;
    private String timestamp;

    public MessageItemData(Context context, @Nullable SlackUser sender, String content, String timestamp) {
        super(context, new Identifier(sender != null ? sender.getUserName() : null, content));
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public MessageItemData(Context context, SlackMessagePosted event) {
        super(context, new Identifier(event.getSender() != null ? event.getSender().getUserName() : null, event.getMessageContent()));
        sender = event.getSender();
        timestamp = event.getTimestamp();
    }

    @Nullable
    public SlackUser getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public abstract ItemData.ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent);

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        TextView subtitle = (TextView) holder.v.findViewById(R.id.subtitle);
        if (subtitle != null) {
            if (!(subtitle.getMovementMethod() instanceof LinkMovementMethod))
                subtitle.setMovementMethod(new LinkMovementMethod());

            new Action<String>() {
                @NonNull
                @Override
                public String id() {
                    return "html";
                }

                @Nullable
                @Override
                protected String run() throws InterruptedException {
                    return SlackUtils.getHtmlFromMessage(getButterySlack(), getIdentifier().getSubtitle());
                }

                @Override
                protected void done(@Nullable String result) {
                    TextView subtitle = (TextView) holder.v.findViewById(R.id.subtitle);
                    if (result != null && subtitle != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            subtitle.setText(Html.fromHtml(result, 0));
                        else subtitle.setText(Html.fromHtml(result));
                    }
                }
            }.execute();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
