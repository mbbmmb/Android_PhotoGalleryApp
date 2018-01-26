package android.b.m.photoapp4;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

public class PhotoGridFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private Callbacks mCallbacks;

    private Photo mNewPhoto;
    private File mPhotoFile;
    private boolean mSubTitleVisible = true;
    private static final String SUBTITLE_VISIBLE_SAVED = "subtitle";
    private static final int REQUEST_PHOTO = 0;

    public interface Callbacks {
        void onPhotoSelected(Photo photo);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SUBTITLE_VISIBLE_SAVED, mSubTitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            mSubTitleVisible = savedInstanceState.getBoolean(SUBTITLE_VISIBLE_SAVED);
        }

        View view = inflater.inflate(R.layout.activity_photo_grid, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        PhotoLab photoLab = PhotoLab.get(getActivity());
        List<Photo> mPhotos = photoLab.getPhotos();
        if(mAdapter == null) {
            mAdapter = new PhotoAdapter(mPhotos);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setPhotos(mPhotos);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    public void updateSubtitle() {
        PhotoLab photoLab = PhotoLab.get(getActivity());
        int photoNumber = photoLab.getPhotos().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, photoNumber, photoNumber);
        if(mSubTitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.add_button, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(!mSubTitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_action_button:
                boolean canTakePhoto;
                final Intent capturePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                addPhoto();
                getPhotoFile();
                canTakePhoto = checkCameraAvailabilityAndFileLocation(item, capturePhoto);

                if(canTakePhoto) {
                    Uri uri = getPhotoFileUri();
                    capturePhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    List<ResolveInfo> cameraActivities = getCameraInfo(capturePhoto, uri);

                    for (ResolveInfo activity : cameraActivities) {
                        getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    startActivityForResult(capturePhoto, REQUEST_PHOTO);
                }
                return true;
            case R.id.show_subtitle:
                mSubTitleVisible = !mSubTitleVisible;
                updateSubtitle();
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addPhoto() {
        mNewPhoto = new Photo();
        PhotoLab.get(getActivity()).addPhoto(mNewPhoto);
    }

    private void getPhotoFile() {
        mPhotoFile = (File) PhotoLab.get(getActivity()).getPhotoFile(mNewPhoto);
    }

    private boolean checkCameraAvailabilityAndFileLocation(MenuItem item, Intent capturePhoto) {
        PackageManager packageManager = getActivity().getPackageManager();
        boolean canTakePhoto = mPhotoFile != null && capturePhoto.resolveActivity(packageManager) != null;
        setCameraButton(item, canTakePhoto);
        return canTakePhoto;
    }
    private void setCameraButton(MenuItem item, boolean canTakePhoto) {
        item.setEnabled(canTakePhoto);
    }

    private Uri getPhotoFileUri() {
        Uri uri = FileProvider.getUriForFile(getActivity(), "android.b.m.photoapp4.fileprovider", mPhotoFile);
        return uri;
    }

    private List<ResolveInfo> getCameraInfo(Intent capturePhoto, Uri uri) {
        List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(capturePhoto, PackageManager.MATCH_DEFAULT_ONLY);
        return cameraActivities;
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;
        private Photo mPhoto;
        private File mPhotoFile;

        public PhotoHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.photo_list_item, parent, false));
            mImageView = (ImageView) itemView.findViewById(R.id.text_id);
            itemView.setOnClickListener(this);
        }

        public void bind(Photo photo) {
            mPhoto = photo;
            mPhotoFile = PhotoLab.get(getActivity()).getPhotoFile(photo);
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onPhotoSelected(mPhoto);
        }
    }

    public class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<Photo> mPhotos;
        PhotoAdapter(List<Photo> photos) {
            mPhotos = photos;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new PhotoHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            Photo photo = mPhotos.get(position);
            holder.bind(photo);
        }

        @Override
        public int getItemCount() {
            return mPhotos.size();
        }

        public void setPhotos(List<Photo> photos) {
            mPhotos = photos;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PHOTO) {
            Uri uri = getPhotoFileUri();
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mCallbacks.onPhotoSelected(mNewPhoto);
        }
    }
}
