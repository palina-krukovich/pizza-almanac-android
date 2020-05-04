package com.polinakrukovich.android.pizzaalmanac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.polinakrukovich.android.pizzaalmanac.config.AppConfig;
import com.polinakrukovich.android.pizzaalmanac.databinding.FragmentNewPizzaBinding;
import com.polinakrukovich.android.pizzaalmanac.model.Pizza;
import com.polinakrukovich.android.pizzaalmanac.observer.SettingsObserver;

public class NewPizzaFragment extends DialogFragment implements
        View.OnClickListener,
        SettingsObserver {
    public static final String TAG = "NewPizzaDialog";

    private FragmentNewPizzaBinding mBinding;

    private static final String KEY_FILE_URI = "key_file_uri";
    private static final int RC_TAKE_PICTURE = 101;

    private Uri mFileUri = null;

    private StorageReference mStorageRef;

    interface NewPizzaListener {
        void onNewPizza(Pizza pizza);
    }

    private NewPizzaListener mNewPizzaListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentNewPizzaBinding.inflate(inflater, container, false);

        mBinding.newPizzaSubmit.setOnClickListener(this);
        mBinding.newPizzaCancel.setOnClickListener(this);
        mBinding.buttonCamera.setOnClickListener(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        AppConfig.getInstance().addObserver(this);
        updateBackground(AppConfig.getInstance().getBackgroundColorId());

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppConfig.getInstance().removeObserver(this);
        mBinding = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof NewPizzaListener) {
            mNewPizzaListener = (NewPizzaListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void onSubmitClicked(View view) {
        Pizza pizza = new Pizza();
        try {
            pizza.setName(mBinding.pizzaNameEdit.getText().toString());
            pizza.setOrigin((String) mBinding.pizzaOriginSpinner.getSelectedItem());
            pizza.setIngredients(mBinding.pizzaIngredientsEdit.getText().toString());
            pizza.setDescription(mBinding.pizzaDescriptionEdit.getText().toString());
            pizza.setRating(mBinding.pizzaRatingBar.getRating());
            pizza.setPhoto(mBinding.pictureUrl.getText().toString());
        } catch (Exception e) {
            dismiss();
        }

        if (mNewPizzaListener != null) {
            mNewPizzaListener.onNewPizza(pizza);
        }

        dismiss();
    }

    private void onCancelClicked(View view) {
        dismiss();
    }

    private void onLaunchCamera() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                mFileUri = data.getData();

                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                }
            }
        }
    }

    private void uploadFromUri(Uri fileUri) {
        final StorageReference ref = mStorageRef.child(fileUri.getLastPathSegment());
        UploadTask uploadTask = ref.putFile(fileUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    mBinding.pictureUrl.setText("Can't upload image");
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    mBinding.pictureUrl.setText(downloadUri.toString());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newPizzaSubmit:
                onSubmitClicked(v);
                break;
            case R.id.newPizzaCancel:
                onCancelClicked(v);
                break;
            case R.id.buttonCamera:
                onLaunchCamera();
                break;
        }
    }

    @Override
    public void updateBackground(int id) {
        mBinding.newPizzaDialog.setBackgroundColor(getResources().getColor(id, null));
    }

    @Override
    public void updateFontSize(int size) {

    }

    @Override
    public void updateFontFamily(String family) {

    }
}
