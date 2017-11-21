package com.example.user.insta.Utils;

import android.media.Image;
        import android.os.Bundle;
        import android.os.Parcelable;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.design.widget.BottomNavigationView;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.GestureDetector;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.CalendarView;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.user.insta.Models.Like;
        import com.example.user.insta.R;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.google.firebase.database.ValueEventListener;
        import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;

        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.Locale;
        import java.util.TimeZone;

        import com.example.user.insta.Utils.BottomNavigationViewHelper;
        import com.example.user.insta.Utils.FirebaseMethods;
        import com.example.user.insta.Utils.GridImageAdapter;
        import com.example.user.insta.Utils.SquareImageView;
        import com.example.user.insta.Utils.UniversalImageLoader;
        import com.example.user.insta.Models.Photo;
        import com.example.user.insta.Models.User;
        import com.example.user.insta.Models.UserAccountSettings;


public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";



    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;


    //widgets
    private SquareImageView mPostImage;
    private BottomNavigationViewEx bottomNavigationView;
    private TextView mBackLabel, mCaption, mUsername, mTimestamp, mLikes;
    private ImageView mBackArrow, mElipses, mHeartRed, mHeartWhite, mProfileImage, mComment;


    //vars
    private Photo mPhoto;
    private int mActivityNumber = 0;
    private String photoUsername = "";
    private String profilePhotoUrl = "";
    private UserAccountSettings mUserAccountSettings;
    private GestureDetector mGestureDetector;
    private Heart mHeart;
    private boolean mLikedByCurrentUser;
    private StringBuilder mUsers;
    private String mLikesString = "";

    public interface OnCommentThreadSelectedListener{
        void onCommentThreadSelectedListener(Photo photo);
    }

    OnCommentThreadSelectedListener onCommentThreadSelectedListener;

    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        mPostImage = (SquareImageView) view.findViewById(R.id.post_image);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
        mBackLabel = (TextView) view.findViewById(R.id.tvBackLabel);
        mCaption = (TextView) view.findViewById(R.id.image_caption);
        mUsername = (TextView) view.findViewById(R.id.username);
        mTimestamp = (TextView) view.findViewById(R.id.image_time_posted);
        mElipses = (ImageView) view.findViewById(R.id.ivElipses);
        mHeartRed = (ImageView) view.findViewById(R.id.image_heart_red);
        mHeartWhite = (ImageView) view.findViewById(R.id.image_heart);
        mProfileImage = (ImageView) view.findViewById(R.id.profile_photo);
        mLikes = (TextView) view.findViewById(R.id.image_likes);
        mComment = (ImageView) view.findViewById(R.id.speech_bubble);

        mHeart = new Heart(mHeartWhite, mHeartRed);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());

        try{
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumFromBundle();
            getPhotoDetails();
            getLikesString();

            setupFirebaseAuth();
            setupBottomNavigationView();

        }catch (Exception e){
            //Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }



        return view;
    }



    private void getLikesString(){

        try {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.dbname_photos))
                    .child(mPhoto.getPhoto_id())
                    .child(getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mUsers = new StringBuilder();
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(getString(R.string.dbname_users))
                                .orderByChild(getString(R.string.field_user_id))
                                .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                    mUsers.append(singleSnapshot.getValue(User.class).getUsername());
                                    mUsers.append(",");

                                }

                                String[] splitUsers = mUsers.toString().split(",");

                                if (mUsers.toString().contains(mUserAccountSettings.getUsername() + ",")) {
                                    mLikedByCurrentUser = true;
                                } else {
                                    mLikedByCurrentUser = false;
                                }

                                int length = splitUsers.length;
                                if (length == 1) {
                                    mLikesString = "Liked by " + splitUsers[0];
                                } else if (length == 2) {
                                    mLikesString = "Liked by " + splitUsers[0]
                                            + " and " + splitUsers[1];
                                } else if (length == 3) {
                                    mLikesString = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + " and " + splitUsers[2];
                                } else if (length == 4) {
                                    mLikesString = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + ", " + splitUsers[2]
                                            + " and " + splitUsers[3];

                                } else if (length > 4) {
                                    mLikesString = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + ", " + splitUsers[2]
                                            + " and " + (splitUsers.length - 3) + " others";

                                }
                                setupWidgets();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    if (!dataSnapshot.exists()) {
                        mLikesString = "";
                        mLikedByCurrentUser = false;
                        setupWidgets();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){

        }
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            //mHeart.toggleLike();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            try {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query = reference
                        .child(getString(R.string.dbname_photos))
                        .child(mPhoto.getPhoto_id())
                        .child(getString(R.string.field_likes));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                            String keyID = singleSnapshot.getKey();
                            //case 1: user already liked the photo
                            if (mLikedByCurrentUser && singleSnapshot.getValue(Like.class).getUser_id()
                                    .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                myRef.child(getString(R.string.dbname_photos))
                                        .child(mPhoto.getPhoto_id())
                                        .child(getString(R.string.field_likes))
                                        .child(keyID)
                                        .removeValue();
                                myRef.child(getString(R.string.dbname_users_photos))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mPhoto.getPhoto_id())
                                        .child(getString(R.string.field_likes))
                                        .child(keyID)
                                        .removeValue();

                                mHeart.toggleLike();
                                getLikesString();
                            }
                            //case 2: user has not liked the photo
                            else if (!mLikedByCurrentUser) {
                                //add new likes
                                addNewLike();
                                break;
                            }
                        }
                        if (!dataSnapshot.exists()) {
                            //add new like
                            addNewLike();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }catch (Exception e1){
                //Toast.makeText(getActivity(),"Area 2",Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }

    private void addNewLike(){

        try {

            String newLikeID = myRef.push().getKey();
            Like like = new Like();
            like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

            myRef.child(getString(R.string.dbname_photos))
                    .child(mPhoto.getPhoto_id())
                    .child(getString(R.string.field_likes))
                    .child(newLikeID)
                    .setValue(like);
            myRef.child(getString(R.string.dbname_users_photos))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(mPhoto.getPhoto_id())
                    .child(getString(R.string.field_likes))
                    .child(newLikeID)
                    .setValue(like);

            mHeart.toggleLike();
            getLikesString();
        }catch (Exception e){
           // Toast.makeText(getActivity(),"Area 3",Toast.LENGTH_SHORT).show();
        }
    }

    private void getPhotoDetails(){
        Log.d(TAG, "getPhotoDetails: retrieving photo details.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    mUserAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);
                }
                //setupWidgets();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    private void setupWidgets(){
        try {

           /*String timestampDiff = getTimestampDifference();
           if (!timestampDiff.equals("0")) {
               mTimestamp.setText(timestampDiff + " DAYS AGO");
           } else {
               mTimestamp.setText("TODAY");
           }*/
            UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(), mProfileImage, null, "");
            mUsername.setText(mUserAccountSettings.getUsername());
            mLikes.setText(mLikesString);
            mCaption.setText(mPhoto.getCaption());

            mBackArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            mComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });

            if (mLikedByCurrentUser){
                mHeartWhite.setVisibility(View.GONE);
                mHeartRed.setVisibility(View.VISIBLE);
                mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return mGestureDetector.onTouchEvent(motionEvent);
                    }
                });
            }
            else {
                mHeartWhite.setVisibility(View.VISIBLE);
                mHeartRed.setVisibility(View.GONE);
                mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return mGestureDetector.onTouchEvent(motionEvent);
                    }
                });
            }

        }catch (Exception e){

            //Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }




    }

    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference() {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = mPhoto.getDate_created();
        try {
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage());
            difference = "0";
        }
        return difference;
    }

    /**
     * retrieve the activity number from the incoming bundle from profileActivity interface
     * @return
     */
    private int getActivityNumFromBundle(){
        Log.d(TAG, "getActivityNumFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));
        }else{
            return 0;
        }
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private Photo getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        }else{
            return null;
        }
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(getActivity(),getActivity() ,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }

       /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
