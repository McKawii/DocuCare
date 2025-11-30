package com.example.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttachmentsRecyclerViewAdapter extends RecyclerView.Adapter<AttachmentsRecyclerViewAdapter.AttachmentsViewHolder>{

    private List<String> attachments;

    private AttachmentClickInterface attachmentClickInterface;

    public AttachmentsRecyclerViewAdapter(AttachmentClickInterface attachmentClickInterface) {
        this.attachmentClickInterface = attachmentClickInterface;
    }


    @NonNull
    @Override
    public AttachmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_item, parent, false);
        return new AttachmentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentsViewHolder holder, int position) {
        String attachment = attachments.get(position);
        holder.attachmentName.setText(attachment);
        holder.clearAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentClickInterface.onRemoveAttachmentClick(attachment);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentClickInterface.onAttachmentClick(attachment);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(attachments == null)
            return 0;
        return attachments.size();
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
        notifyDataSetChanged();
    }

    static class AttachmentsViewHolder extends RecyclerView.ViewHolder {

        private TextView attachmentName;
        private ImageView clearAttachment;

        public AttachmentsViewHolder(@NonNull View itemView) {
            super(itemView);
            attachmentName = itemView.findViewById(R.id.textAttachmentItem);
            clearAttachment = itemView.findViewById(R.id.imageViewClearAttachment);

        }
    }
}
