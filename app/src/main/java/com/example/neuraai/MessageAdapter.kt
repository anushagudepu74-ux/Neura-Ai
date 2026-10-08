package com.example.neuraai

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val container: LinearLayout = view.findViewById(R.id.messageContainer)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)

        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int
    ) {

        val message = messages[position]

        holder.tvMessage.text = message.message

        if (message.isUser) {

            holder.container.gravity = Gravity.END

            holder.tvMessage.setBackgroundResource(
                R.drawable.bg_user_message
            )

            holder.tvMessage.setTextColor(
                holder.itemView.context.getColor(
                    R.color.white
                )
            )

        } else {

            holder.container.gravity = Gravity.START

            holder.tvMessage.setBackgroundResource(
                R.drawable.bg_ai_message
            )

            holder.tvMessage.setTextColor(
                holder.itemView.context.getColor(
                    R.color.text_primary
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}