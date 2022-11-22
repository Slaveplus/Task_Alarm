package kr.ac.jbnu.se.taskalarm.adapter;

import android.graphics.Paint;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_alarm.databinding.ItemTaskBinding;

import kr.ac.jbnu.se.taskalarm.model.Task;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskItemViewHolder> {

    private Consumer<Task> onCheckedChangeListener;

    public TaskAdapter() {
        super(new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return TextUtils.equals(oldItem.documentId, newItem.documentId);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return TextUtils.equals(oldItem.name, newItem.name) && oldItem.done == newItem.done;
            }

            @Override
            public Object getChangePayload(@NonNull Task oldItem, @NonNull Task newItem) {
                return new Object();
            }
        });
    }

    public void setOnCheckedChangeListener(Consumer<Task> onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    @NonNull
    @Override
    public TaskItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTaskBinding binding = ItemTaskBinding.inflate(inflater, parent, false);
        return new TaskItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskItemViewHolder holder, int position) {
        Task item = getItem(position);
        ItemTaskBinding binding = holder.binding;

        binding.nameTextView.setText(item.name);
        if (item.done) {
            binding.nameTextView.setPaintFlags(binding.nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            binding.nameTextView.setPaintFlags(0);
        }

        binding.rangeTextView.setText(DateUtils.formatDateRange(
                binding.getRoot().getContext(),
                item.from.getTime(),
                item.to.getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_NUMERIC_DATE
        ));

        binding.checkBox.setOnCheckedChangeListener(null);
        binding.checkBox.setChecked(item.done);
        binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            item.done = !item.done;
//            if (item.done) {
//                binding.nameTextView.setPaintFlags(binding.nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            } else {
//                binding.nameTextView.setPaintFlags(0);
//            }

            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.accept(item);
            }
        });
    }

    static class TaskItemViewHolder extends RecyclerView.ViewHolder {
        public final ItemTaskBinding binding;

        public TaskItemViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
