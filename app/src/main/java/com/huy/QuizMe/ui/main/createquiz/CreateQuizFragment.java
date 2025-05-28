package com.huy.QuizMe.ui.main.createquiz;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.bumptech.glide.Glide;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Category;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.ui.main.createquiz.adapter.SelectionAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 100;

    private Toolbar toolbar;
    private ImageView imgCover;
    private TextView tvAddCoverImage;
    private EditText etTitle, etDescription;
    private TextView tvSelectedCollection, tvSelectedVisibility;
    private Button btnSave, btnAddQuestion;
    private ConstraintLayout collectionSelector, visibilitySelector;

    private Uri coverImageUri = null;
    private List<Category> categories = new ArrayList<>();
    private Quiz newQuiz = new Quiz();

    public static CreateQuizFragment newInstance() {
        return new CreateQuizFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();
        loadCategories();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbarCreateQuiz);
        imgCover = view.findViewById(R.id.imgCover);
        tvAddCoverImage = view.findViewById(R.id.tvAddCoverImage);
        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        tvSelectedCollection = view.findViewById(R.id.tvSelectedCollection);
        tvSelectedVisibility = view.findViewById(R.id.tvSelectedVisibility);
        btnSave = view.findViewById(R.id.btnSave);
        btnAddQuestion = view.findViewById(R.id.btnAddQuestion);
        collectionSelector = view.findViewById(R.id.collectionSelector);
        visibilitySelector = view.findViewById(R.id.visibilitySelector);

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupListeners() {
        // Cover image selection
        View.OnClickListener coverImageClickListener = v -> openImagePicker();
        imgCover.setOnClickListener(coverImageClickListener);
        tvAddCoverImage.setOnClickListener(coverImageClickListener);

        // Collection selector
        collectionSelector.setOnClickListener(v -> showCollectionPicker());

        // Visibility selector
        visibilitySelector.setOnClickListener(v -> showVisibilityPicker());

        // Save button
        btnSave.setOnClickListener(v -> saveQuiz());

        // Add Question button
        btnAddQuestion.setOnClickListener(v -> navigateToAddQuestion());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void loadCategories() {
        // Here you would typically load categories from your repository
        // For example:
        // viewModel.getCategories().observe(getViewLifecycleOwner(), result -> {
        //    if (result != null && result.isSuccess()) {
        //        categories = result.getData();
        //    }
        // });
    }

    private void showCollectionPicker() {
        // Show a bottom sheet or dialog with collections
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_selection, null);

        TextView tvTitle = bottomSheetView.findViewById(R.id.tvTitle);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recyclerView);

        tvTitle.setText("Select Collection");

        // Convert categories to string names
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        // If no categories loaded yet, add some defaults
        if (categoryNames.isEmpty()) {
            categoryNames.add("Education");
            categoryNames.add("Technology");
            categoryNames.add("Entertainment");
            categoryNames.add("Sports");
            categoryNames.add("Science");
        }

        SelectionAdapter adapter = new SelectionAdapter(categoryNames, (item, position) -> {
            tvSelectedCollection.setText(item);
            if (position < categories.size()) {
                newQuiz.setCategoryId(categories.get(position).getId());
                newQuiz.setCategoryName(item);
            }
            dialog.dismiss();
        });

        recyclerView.setAdapter(adapter);
        dialog.setContentView(bottomSheetView);
        dialog.show();
    }

    private void showVisibilityPicker() {
        // Show a bottom sheet or dialog with visibility options
        List<String> visibilityOptions = new ArrayList<>();
        visibilityOptions.add("Only me");
        visibilityOptions.add("Public");
        visibilityOptions.add("Shared with friends");

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_selection, null);

        TextView tvTitle = bottomSheetView.findViewById(R.id.tvTitle);
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recyclerView);

        tvTitle.setText("Select Visibility");

        SelectionAdapter adapter = new SelectionAdapter(visibilityOptions, (item, position) -> {
            tvSelectedVisibility.setText(item);
            newQuiz.setIsPublic(position == 1); // Only "Public" option is true
            dialog.dismiss();
        });

        recyclerView.setAdapter(adapter);
        dialog.setContentView(bottomSheetView);
        dialog.show();
    }

    private void saveQuiz() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            return;
        }

        newQuiz.setTitle(title);
        newQuiz.setDescription(description);

        // Upload cover image if selected
        if (coverImageUri != null) {
            // Upload image and get URL
            // For example:
            // uploadImage(coverImageUri, url -> {
            //     newQuiz.setQuizThumbnails(url);
            //     saveQuizToServer();
            // });
        } else {
            // saveQuizToServer();
        }

        Toast.makeText(requireContext(), "Quiz saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void navigateToAddQuestion() {
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Title is required");
            return;
        }

        // Navigate to add question screen with the current quiz info
        // For example:
        // Bundle bundle = new Bundle();
        // bundle.putSerializable("quiz", newQuiz);
        // Navigation.findNavController(requireView()).navigate(R.id.action_to_add_question, bundle);

        Toast.makeText(requireContext(), "Navigate to add question", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            coverImageUri = data.getData();
            try {
                // Load the selected image
                Glide.with(this)
                        .load(coverImageUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_image)
                        .into(imgCover);

                // Update layout to show that an image is selected
                tvAddCoverImage.setText("Change Cover Image");

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
