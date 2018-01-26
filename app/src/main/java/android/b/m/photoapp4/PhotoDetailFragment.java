package android.b.m.photoapp4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class PhotoDetailFragment extends Fragment {
    private Photo mPhoto;
    private File mPhotoFile;
    private Callbacks mCallbacksDetail;

    private EditText mEditTextView;
    private TextView mTextView;
    private ImageView mPhotoView;
    private TextView mDateView;
    private CheckBox mCheckBox;

    private String mDateString;
    private static final String ARG_PHOTO_ID = "photo_id";
    private static final String DIALOG_DATE = "dialogDate";
    private static final int REQUEST_DATE = 0;

    public interface Callbacks {
        void onPhotoUpdated(Photo photo);
    }

    public static PhotoDetailFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_ID, id);
        PhotoDetailFragment fragment = new PhotoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacksDetail = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID photoId = (UUID) getArguments().getSerializable(ARG_PHOTO_ID);
        mPhoto = PhotoLab.get(getActivity()).getPhoto(photoId);
        mPhotoFile = PhotoLab.get(getActivity()).getPhotoFile(mPhoto);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        PhotoLab.get(getActivity()).updatePhoto(mPhoto);
    }

    public void updatePhoto() {
        PhotoLab.get(getActivity()).updatePhoto(mPhoto);
        mCallbacksDetail.onPhotoUpdated(mPhoto);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.delete_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.delete_photo:
                PhotoLab photoLab = PhotoLab.get(getActivity());
                photoLab.deletePhoto(mPhoto);
                updatePhoto();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.photo_detail, container, false);

        mTextView = (TextView) view.findViewById(R.id.title);
        mTextView.setText(mPhoto.getTitle());

        mPhotoView = (ImageView) view.findViewById(R.id.photoView);
        if(mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }

        mCheckBox = (CheckBox) view.findViewById(R.id.checkbox);
        mCheckBox.setChecked(mPhoto.getChecked());
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPhoto.setChecked(isChecked);
                updatePhoto();
                if(mPhoto.getChecked()) {
                    mEditTextView.setVisibility(View.VISIBLE);
                } else {
                    mEditTextView.setVisibility(View.GONE);
                }
            }
        });

        mDateView = (TextView) view.findViewById(R.id.date);
        setDateTextView(mPhoto.getDate());
        mDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment fragment = DatePickerFragment.newInstance(mPhoto.getDate());
                fragment.setTargetFragment(PhotoDetailFragment.this, REQUEST_DATE);
                fragment.show(fm, DIALOG_DATE);
            }
        });

        mEditTextView = (EditText) view.findViewById(R.id.edit_title);
        if(!mPhoto.getChecked()) {
            mEditTextView.setVisibility(View.GONE);
        }
        if(mPhoto.getTitle() != null) {
            mEditTextView.setHint(mPhoto.getTitle());
        }
        mEditTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEditTextView.setHint("");
                return false;
            }
        });
        mEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhoto.setTitle(s.toString());
                mTextView.setText(mPhoto.getTitle());
                updatePhoto();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mPhoto.setDate(date);
            updatePhoto();
            setDateTextView(date);
        }
    }

    private void setDateTextView(Date date) {
        mDateString = DateFormat.getDateInstance().format(date);
        mDateView.setText(mDateString);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacksDetail = null;
    }
}
