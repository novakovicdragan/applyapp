package com.devdroid.dragan.draganapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devdroid.dragan.draganapp.R;
import com.devdroid.dragan.draganapp.model.Comment;
import com.devdroid.dragan.draganapp.model.Photo;
import com.devdroid.dragan.draganapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    // Declare variables for all view types. There is just one user so I'm using just User object
    private User user;
    private List<Comment> list_comments;
    private List<Photo> list_photos;

    // Declare default values for all view types we expect
    private final int VIEW_LOADER = 0;
    private final int VIEW_NO_DATA = 1;
    private final int VIEW_HEADER_USER = 2;
    private final int VIEW_USER = 3;
    private final int VIEW_HEADER_COMMENTS = 4;
    private final int VIEW_COMMENT = 5;
    private final int VIEW_HEADER_PHOTOS = 6;
    public final int VIEW_PHOTO = 7;

    // Flags for loaders for each category
    private boolean isLoadingUser = false;
    private boolean isLoadingComments = false;
    private boolean isLoadingPhotos = false;

    // Receives context and initialize list variables
    public DetailsAdapter(Context context) {
        this.context = context;
        user = new User();
        list_comments = new ArrayList<>();
        list_photos = new ArrayList<>();
    }

    /**
     *
     * @param user
     * Received response, set loading flag indicator to false
     */
    public void setUser(User user) {
        isLoadingUser = false;
        if (user.isSet()) { // Check if user is actually set, or we need to show No-data view
            this.user = user;
        }
        this.notifyDataSetChanged();
    }

    /**
     *
     * @param comments
     * Set loading flag indicator to false
     * Check if list has comments, add all of them at once and refresh adapter
     */
    public void addCommentsList(List<Comment> comments) {
        isLoadingComments = false;
        if (comments.size() > 0) {
            list_comments.addAll(comments);
        }
        this.notifyDataSetChanged();
    }

    /**
     *
     * @param photos
     * Set loading flag indicator to false
     * Check if list has photos, add all of them at once and refresh recycler view
     */
    public void addPhotosList(List<Photo> photos) {
        isLoadingPhotos = false;
        if (photos.size() > 0) {
            list_photos.addAll(photos);
        }
        this.notifyDataSetChanged();
    }

    /**
     *
     * @param active
     * Show or disable loading bar indicator for user
     */
    public void setLoadingUserIndicator(boolean active) {
        isLoadingUser = active;
        this.notifyDataSetChanged();
    }

    /**
     *
     * @param active
     * If active and list_comments has data, that means we are requesting data
     * for another user or post.
     */
    public void setLoadingCommentsIndicator(boolean active) {
        isLoadingComments = active;
        if (list_comments.size() > 0 && active) {
            list_comments.clear();
        }
        this.notifyDataSetChanged();
    }

    /**
     *
     * @param active
     * If active and list_photos has data, that means we are requesting data
     * for another user or post.
     */
    public void setLoadingPhotosIndicator(boolean active) {
        isLoadingPhotos = active;
        if (list_photos.size() > 0) {
            list_photos.clear();
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        //Total 4 is for: 3 default headers plus 1 fix (user or loader/noData view)
        return 4 + (list_comments.size() > 0 ? list_comments.size() : 1) + (list_photos.size() > 0 ? list_photos.size() : 1);
    }

    public class ViewHolderUser extends RecyclerView.ViewHolder {
        TextView txvUserName;
        TextView txvEmail;
        TextView txvPhone;
        TextView txvWebSite;

        public ViewHolderUser(View itemView) {
            super(itemView);
            txvUserName = (TextView) itemView.findViewById(R.id.userName);
            txvEmail = (TextView) itemView.findViewById(R.id.userMail);
            txvPhone = (TextView) itemView.findViewById(R.id.userPhone);
            txvWebSite = (TextView) itemView.findViewById(R.id.userWebsite);
        }
    }


    public class ViewHolderComment extends RecyclerView.ViewHolder {
        TextView txvTitle;
        TextView txvComment;

        public ViewHolderComment(View itemView) {
            super(itemView);
            txvTitle = (TextView) itemView.findViewById(R.id.title);
            txvComment = (TextView) itemView.findViewById(R.id.comment);
        }
    }

    public class ViewHolderPhoto extends RecyclerView.ViewHolder {
        ImageView imvPhoto;

        public ViewHolderPhoto(View itemView) {
            super(itemView);
            imvPhoto = (ImageView) itemView.findViewById(R.id.detailPhoto);
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        TextView txvTitle;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            txvTitle = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public class ViewHolderLoading extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public ViewHolderLoading(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    public class ViewHolderNoData extends RecyclerView.ViewHolder {
        ImageView imvNoData;
        TextView txvNoData;

        public ViewHolderNoData(View itemView) {
            super(itemView);
            imvNoData = (ImageView) itemView.findViewById(R.id.icon_nodata);
            txvNoData = (TextView) itemView.findViewById(R.id.info_nodata);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) { // First position is always fixed to user header
            return VIEW_HEADER_USER;
        } else if (position == 1) { // Second position is always fixed to one - user / loader / nodata
            if (user.isSet()) { // check flag on user object if its set
                return VIEW_USER;
            } else {
                return isLoadingUser ? VIEW_LOADER : VIEW_NO_DATA;
            }
        } else if (position == 2) { // Third position  is always fixed to comments category header
            return VIEW_HEADER_COMMENTS;
        } else if (position == 3) { // Forth position is first comment in row OR loader / nodata
            if (list_comments.size() > 0) { // if there is data in list_comments then set view type comment
                return VIEW_COMMENT;
            } else {
                return isLoadingComments ? VIEW_LOADER : VIEW_NO_DATA;
            }
        } else if (position - list_comments.size() < 3) { // if its inside this scope then its view type comment
            return VIEW_COMMENT;
        } else if (position == (list_comments.size() > 0 ? list_comments.size() : 1) + 3) {
            // calculate size of comments and first fixed positions for photo category header
            return VIEW_HEADER_PHOTOS;
        } else if (position == (list_comments.size() > 0 ? list_comments.size() : 1) + 4) {
            // calculate to check if first item after photo header is view type photo or loader / nodata
            if (list_photos.size() > 0) {
                return VIEW_PHOTO;
            } else {
                return isLoadingPhotos ? VIEW_LOADER : VIEW_NO_DATA;
            }
        } else {
            return VIEW_PHOTO;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_LOADER:
                return new ViewHolderLoading(LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false));
            case VIEW_NO_DATA:
                return new ViewHolderNoData(LayoutInflater.from(parent.getContext()).inflate(R.layout.details_nodata_item, parent, false));
            case VIEW_HEADER_USER:
            case VIEW_HEADER_COMMENTS:
            case VIEW_HEADER_PHOTOS:
                return new ViewHolderHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.detailsheader_item, parent, false));
            case VIEW_USER:
                return new ViewHolderUser(LayoutInflater.from(parent.getContext()).inflate(R.layout.detailsuser_item, parent, false));
            case VIEW_COMMENT:
                return new ViewHolderComment(LayoutInflater.from(parent.getContext()).inflate(R.layout.details_comment_item, parent, false));
            case VIEW_PHOTO:
                return new ViewHolderPhoto(LayoutInflater.from(parent.getContext()).inflate(R.layout.detailsphoto_item, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ViewHolderHeader) {
            ViewHolderHeader holder = (ViewHolderHeader) viewHolder;

            if (position == 0) {
                holder.txvTitle.setText(R.string.header_user);
            } else if (position == 2) {
                holder.txvTitle.setText(R.string.header_comments);
            } else {
                holder.txvTitle.setText(R.string.header_photos);
            }


        } else if (viewHolder instanceof ViewHolderUser) {
            ViewHolderUser holder = (ViewHolderUser) viewHolder;

            holder.txvUserName.setText(user.getName());
            holder.txvEmail.setText(user.getEmail());
            holder.txvPhone.setText(user.getPhone());
            holder.txvWebSite.setText(user.getWebAddress());

        } else if (viewHolder instanceof ViewHolderComment) {
            ViewHolderComment holder = (ViewHolderComment) viewHolder;

            Comment comment = list_comments.get(position - 3);

            holder.txvTitle.setText(comment.getTitle());
            holder.txvComment.setText(comment.getBody());

        } else if (viewHolder instanceof ViewHolderPhoto) {
            ViewHolderPhoto holder = (ViewHolderPhoto) viewHolder;

            Photo photo = list_photos.get(position - (list_comments.size() > 0 ? list_comments.size() : 1) - 4);

            //Using fast and lightweight picasso library for image loading
            Picasso
                    .with(context.getApplicationContext())
                    .load(photo.getThumbnailUrl())
                    .placeholder(R.drawable.ic_image_black)
                    .into(holder.imvPhoto);


        }


    }

}
