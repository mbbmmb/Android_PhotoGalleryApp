package android.b.m.photoapp4;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class PhotoGridActivity extends SingleFragmentActivity
        implements PhotoGridFragment.Callbacks, PhotoDetailFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new PhotoGridFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onPhotoSelected(Photo photo) {
        if(findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = PhotoPagerActivity.newIntent(this, photo.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = PhotoDetailFragment.newInstance(photo.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onPhotoUpdated(Photo photo) {
        PhotoGridFragment listFragment = (PhotoGridFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
