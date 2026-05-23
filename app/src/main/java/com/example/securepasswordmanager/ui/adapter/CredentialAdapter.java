package com.example.securepasswordmanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.securepasswordmanager.R;
import com.example.securepasswordmanager.data.local.Credential;
import com.example.securepasswordmanager.security.CryptoHelper;
import com.example.securepasswordmanager.security.SecurityManager;
import android.view.animation.AnimationUtils;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;

public class CredentialAdapter extends RecyclerView.Adapter<CredentialAdapter.ViewHolder> {

    private List<Credential> allCredentials = new ArrayList<>();
    private List<Credential> filteredCredentials = new ArrayList<>();
    private final OnItemClickListener listener;
    private int lastPosition = -1;

    public interface OnItemClickListener {
        void onCopyClick(Credential credential);
        void onDeleteClick(Credential credential);
    }

    public CredentialAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setCredentials(List<Credential> credentials) {
        this.allCredentials = credentials;
        this.filteredCredentials = new ArrayList<>(credentials);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredCredentials.clear();
        SecretKeySpec key = SecurityManager.getInstance().getSecretKey();
        if (query.isEmpty()) {
            filteredCredentials.addAll(allCredentials);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Credential item : allCredentials) {
                try {
                    String decryptedService = CryptoHelper.decrypt(item.getServiceName(), key).toLowerCase();
                    if (decryptedService.contains(lowerCaseQuery)) {
                        filteredCredentials.add(item);
                    }
                } catch (Exception ignored) {}
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credential, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Credential credential = filteredCredentials.get(position);
        SecretKeySpec key = SecurityManager.getInstance().getSecretKey();

        try {
            holder.tvService.setText(CryptoHelper.decrypt(credential.getServiceName(), key));
            holder.tvUser.setText(CryptoHelper.decrypt(credential.getUsername(), key));
            String category = credential.getCategory();
            if (category != null && !category.isEmpty()) {
                holder.chipCategory.setText(category);
                holder.chipCategory.setVisibility(View.VISIBLE);
            } else {
                holder.chipCategory.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.tvService.setText(holder.itemView.getContext().getString(R.string.error_decrypting));
            holder.tvUser.setText("****");
            holder.chipCategory.setVisibility(View.GONE);
        }

        holder.btnCopy.setOnClickListener(v -> listener.onCopyClick(credential));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(credential));

        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            android.view.animation.Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return filteredCredentials.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvService, tvUser;
        Chip chipCategory;
        ImageButton btnCopy, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvService = itemView.findViewById(R.id.tvService);
            tvUser = itemView.findViewById(R.id.tvUser);
            chipCategory = itemView.findViewById(R.id.chipCategory);
            btnCopy = itemView.findViewById(R.id.btnCopy);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
