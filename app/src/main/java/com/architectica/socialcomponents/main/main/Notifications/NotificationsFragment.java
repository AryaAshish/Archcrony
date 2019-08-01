package com.architectica.socialcomponents.main.main.Notifications;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.architectica.socialcomponents.R;
import com.architectica.socialcomponents.adapters.NotificationsAdapter;
import com.architectica.socialcomponents.adapters.PostsAdapter;
import com.architectica.socialcomponents.main.base.BaseFragment;
import com.architectica.socialcomponents.main.main.MainActivity;
import com.architectica.socialcomponents.main.postDetails.PostDetailsActivity;
import com.architectica.socialcomponents.main.profile.ProfileActivity;
import com.architectica.socialcomponents.model.Post;
import com.architectica.socialcomponents.utils.AnimationUtils;

public class NotificationsFragment extends BaseFragment<NotificationsView,NotificationsPresenter> implements NotificationsView{

    private NotificationsAdapter postsAdapter;
    private RecyclerView recyclerView;

    private TextView newPostsCounterTextView;
    private boolean counterAnimationInProgress = false;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeContainer;

    public NotificationsFragment(){

    }

    public static Fragment newInstance() {

        Fragment frag = new NotificationsFragment();

        Bundle args = new Bundle();

        frag.setArguments(args);

        return frag;

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.updateNewPostCounter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        initContentView(view);

        return view;
    }

    @Override
    public NotificationsPresenter createPresenter() {

        if (presenter == null) {
            return new NotificationsPresenter(getActivity());
        }
        return presenter;

    }

    public void refreshPostList() {
        postsAdapter.loadAdminPostsFirstPage();
        if (postsAdapter.getItemCount() > 0) {
            recyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void showCounterView(int count) {
        AnimationUtils.showViewByScaleAndVisibility(newPostsCounterTextView);
        String counterFormat = getResources().getQuantityString(R.plurals.new_posts_counter_format, count, count);
        newPostsCounterTextView.setText(String.format(counterFormat, count));
    }

    private void initContentView(View view) {
        if (recyclerView == null) {
            progressBar = view.findViewById(R.id.progressBar);
            swipeContainer = view.findViewById(R.id.swipeContainer);

            initPostListRecyclerView(view);
            initPostCounter(view);
        }
    }

    private void initPostListRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        postsAdapter = new NotificationsAdapter((MainActivity) getActivity(), swipeContainer);
        postsAdapter.setCallback(new NotificationsAdapter.Callback() {
            @Override
            public void onItemClick(final Post post, final View view) {
                openPostDetailsActivity(post, view);
//                presenter.onPostClicked(post, view);

            }

            @Override
            public void onListLoadingFinished() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAuthorClick(String authorId, View view) {
                openProfileActivity(authorId, view);
            }

            @Override
            public void onCanceled(String message) {
                progressBar.setVisibility(View.GONE);
                showToast(message);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(postsAdapter);
        postsAdapter.loadAdminPostsFirstPage();
    }

    private void initPostCounter(View view) {
        newPostsCounterTextView = view.findViewById(R.id.newPostsCounterTextView);
        newPostsCounterTextView.setOnClickListener(v -> refreshPostList());

        presenter.initPostCounter();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                hideCounterView();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void hideCounterView() {
        if (!counterAnimationInProgress && newPostsCounterTextView.getVisibility() == View.VISIBLE) {
            counterAnimationInProgress = true;
            AlphaAnimation alphaAnimation = AnimationUtils.hideViewByAlpha(newPostsCounterTextView);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    counterAnimationInProgress = false;
                    newPostsCounterTextView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            alphaAnimation.start();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void openPostDetailsActivity(Post post, View v) {
        Intent intent = new Intent(getActivity(), PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.POST_ID_EXTRA_KEY, post.getId());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            View imageView = v.findViewById(R.id.postImageView);
            View authorImageView = v.findViewById(R.id.authorImageView);

            if (imageView.getVisibility() != View.GONE){

                ActivityOptions options = ActivityOptions.
                        makeSceneTransitionAnimation(getActivity(),
                                new android.util.Pair<>(imageView, getString(R.string.post_image_transition_name)),
                                new android.util.Pair<>(authorImageView, getString(R.string.post_author_image_transition_name))
                        );

                startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST, options.toBundle());

            }
            else {

                startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST);

            }

        } else {
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST);
        }
    }

    @Override
    public void showFloatButtonRelatedSnackBar(int messageId) {
        Toast.makeText(getActivity(), "press back button again to exit", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void openProfileActivity(String userId, View view) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, userId);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            View authorImageView = view.findViewById(R.id.authorImageView);

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(getActivity(),
                            new android.util.Pair<>(authorImageView, getString(R.string.post_author_image_transition_name)));
            startActivityForResult(intent, ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST, options.toBundle());
        } else {
            startActivityForResult(intent, ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST);
        }
    }

}
