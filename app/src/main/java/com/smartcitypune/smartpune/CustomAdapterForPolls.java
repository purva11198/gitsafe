    package com.smartcitypune.smartpune;

    import android.content.Context;
    import android.content.Intent;
    import android.support.v7.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;

    import java.util.List;

    public class CustomAdapterForPolls extends RecyclerView.Adapter<CustomAdapterForPolls.ViewHolder> {

        public List<Poll> values;
        private Context context;

        //firebase initialization
        final FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = mFirebaseDatabase.getReference().child("data/polls");
        private FirebaseAuth mAuth;


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txtHeader;
            public TextView txtFooter;
            public View layout;

            public ViewHolder(View v) {
                super(v);
                layout = v;
                txtHeader = (TextView) v.findViewById(R.id.firstLine);
                txtFooter = (TextView) v.findViewById(R.id.secondLine);
            }

        }
        public void add(int position, Poll item) {
            values.add(position, item);
            notifyItemInserted(position);
        }
        //remove karne k liye rakha h
        public void remove(int position) {
            values.remove(position);
            notifyItemRemoved(position);
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public CustomAdapterForPolls(List<Poll> myDataset) {
            values = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
            // create a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.row_layout, parent, false);
            // set the view's size, margins, paddings and layout parameters
            context = v.getContext();
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            holder.txtHeader.setText(values.get(position).getQuestion());
            holder.txtFooter.setText(values.get(position).getStartdate() + " - " + values.get(position).getEnddate());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String key= currentUser.getUid();
                    if(!values.get(position).getUid().contains(key)){
                    Intent intent = new Intent(v.getContext(), Voting.class);
                    intent.putExtra("Voting",values.get(position));

                    //position is nth question in database
                    Voting voting = new Voting();
                    voting.updateQuesPosition(position);
                   // Log.e("position",""+position);

                    v.getContext().startActivity(intent);
                    }
                    else{
                        Toast.makeText(context,"You already Voted",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return values.size();
        }

    }
