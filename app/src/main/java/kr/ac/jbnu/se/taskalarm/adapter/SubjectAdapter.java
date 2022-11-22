package kr.ac.jbnu.se.taskalarm.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_alarm.databinding.ItemSubjectBinding;

import kr.ac.jbnu.se.taskalarm.model.Subject;
import kr.ac.jbnu.se.taskalarm.model.Task;

public class SubjectAdapter extends ListAdapter<Subject, SubjectAdapter.SubjectItemViewHolder> {

    private Consumer<Task> onCheckedChangeListener;

    public SubjectAdapter() {
        super(new DiffUtil.ItemCallback<Subject>() {
            @Override
            public boolean areItemsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
                return TextUtils.equals(oldItem.documentId, newItem.documentId);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
                if (oldItem.tasks.size() != newItem.tasks.size()) return false;
                if (!TextUtils.equals(oldItem.name, newItem.name)) return false;
                for (int i = 0; i < oldItem.tasks.size(); i++) {
                    Task oldTask = oldItem.tasks.get(i);
                    Task newTask = newItem.tasks.get(i);

                    if (!TextUtils.equals(oldTask.documentId, newTask.documentId)) return false;
                    if (oldTask.done != newTask.done) return false;
                }

                return true;
            }

            @Override
            public Object getChangePayload(@NonNull Subject oldItem, @NonNull Subject newItem) {
                return new Object();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOnCheckedChangeListener(Consumer<Task> onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSubjectBinding binding = ItemSubjectBinding.inflate(inflater, parent, false);
        return new SubjectItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectItemViewHolder holder, int position) {
        Subject item = getItem(position);
        ItemSubjectBinding binding = holder.binding;
        TaskAdapter adapter = holder.adapter;

        binding.nameTextView.setText(item.name);
        adapter.setOnCheckedChangeListener(onCheckedChangeListener);
        adapter.submitList(item.tasks);
    }

    static class SubjectItemViewHolder extends RecyclerView.ViewHolder {
        public final ItemSubjectBinding binding;
        public final TaskAdapter adapter = new TaskAdapter();

        public SubjectItemViewHolder(ItemSubjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.recyclerView.setAdapter(adapter);
        }
    }
}
