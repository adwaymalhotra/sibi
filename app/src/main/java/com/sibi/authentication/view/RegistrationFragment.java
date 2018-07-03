package com.sibi.authentication.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sibi.R;
import com.sibi.authentication._interface.IAuthenticationView;
import com.sibi.authentication._interface.IRegistrationView;
import com.sibi.authentication.presenter.RegistrationPresenter;
import com.sibi.util.Utils;
import com.sibi.util.dagger.AppComponentProvider;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by adway on 28/11/17.
 */

public class RegistrationFragment extends Fragment implements IRegistrationView {
    public static final String TAG = "RegistrationFragment";
    @BindView(R.id.email_et) protected EditText emailET;
    @BindView(R.id.password_et) protected EditText passwordET;
    @BindView(R.id.confirm_password_et) protected EditText confirmPasswordET;
    @BindView(R.id.name_et) protected EditText nameET;
    @BindView(R.id.image_circle) protected ImageView profilePicIV;

    @Inject RegistrationPresenter presenter;
    private IAuthenticationView parentView;

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    presenter.setSelectedBitmap(bitmap);
                    setBitmap(bitmap);
                }
            }
        } else if (requestCode == Utils.REQUEST_GALLERY_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    String imageString;
                    Uri uri = data.getData();

                    Cursor cursor = getContext().getContentResolver()
                        .query(uri, filePathColumn, null, null, null);
                    if (cursor == null) throw new NullPointerException("Cursor is Null");
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageString = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap profilePicBitmap = BitmapFactory.decodeFile(imageString);
                    setBitmap(profilePicBitmap);
                    presenter.setSelectedBitmap(profilePicBitmap);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utils.REQUEST_CAMERA:
                Utils.openCameraForImage(this);
                break;
            case Utils.REQUEST_GALLERY_IMAGE:
                Utils.openGalleryForImage(this);
                break;
        }
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        parentView = ((IAuthenticationView) context);
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        ButterKnife.bind(this, view);
        AppComponentProvider.getAppComponent().inject(this);
        presenter.setView(this);
        return view;
    }

    private void setBitmap(Bitmap bitmap) {
        profilePicIV.setImageBitmap(bitmap);
    }

    @OnClick(R.id.image_circle) public void onImageClick() {
        new AlertDialog.Builder(getContext())
            .setItems(new CharSequence[]{"Snap a picture", "Choose from Gallery"},
                (dialogInterface, i) -> {
                    Fragment f = RegistrationFragment.this;
                    switch (i) {
                        case 0:
                            if (Utils.hasCameraPermission(f) == 1)
                                Utils.openCameraForImage(f);
                            break;
                        case 1:
                            if (Utils.hasStoragePermission(f) == 1)
                                Utils.openGalleryForImage(f);
                            break;
                    }
                }).setNegativeButton("Cancel",
            (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    @OnClick(R.id.register_btn) public void onRegisterClick() {
        presenter.doRegistration();
    }

    @Override public String getEmail() {
        return emailET.getText().toString();
    }

    @Override public String getPassword() {
        return passwordET.getText().toString();
    }

    @Override public String getName() {
        return nameET.getText().toString();
    }

    @Override public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override public void redirectToLogin() {
        getActivity().onBackPressed();
    }
}
