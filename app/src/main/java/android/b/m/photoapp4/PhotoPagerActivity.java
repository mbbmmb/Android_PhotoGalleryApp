package android.b.m.photoapp4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class PhotoPagerActivity extends AppCompatActivity
        implements PhotoDetailFragment.Callbacks {

    public static final String EXTRA_PHOTO_ID = "android.b.m.photoapp4.photo_id";
    private ViewPager mViewPager;
    private List<Photo> mPhotos;

    @Override
    public void onPhotoUpdated(Photo photo) {
    }

    public static Intent newIntent(Context packageContext, UUID photoId) {
        Intent intent = new Intent(packageContext, PhotoPagerActivity.class);
        intent.putExtra(EXTRA_PHOTO_ID, photoId);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);
        mViewPager = (ViewPager) findViewById(R.id.photo_view_pager);
        UUID photoId = (UUID) getIntent().getSerializableExtra(EXTRA_PHOTO_ID);
        mPhotos = PhotoLab.get(this).getPhotos();
        FragmentManager fm = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Photo photo = mPhotos.get(position);
                return PhotoDetailFragment.newInstance(photo.getId());
            }

            @Override
            public int getCount() {
                return mPhotos.size();
            }
        });

        for (int i = 0; i<mPhotos.size(); i++) {
            if(mPhotos.get(i).getId().equals(photoId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
