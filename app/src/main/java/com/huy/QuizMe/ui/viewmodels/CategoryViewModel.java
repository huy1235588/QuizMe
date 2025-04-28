package com.huy.QuizMe.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.huy.QuizMe.data.model.Category;
import com.huy.QuizMe.data.repository.CategoryRepository;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.utils.NetworkUtils;

import java.util.List;

/**
 * ViewModel cho dữ liệu danh mục
 */
public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository categoryRepository;
    private final MediatorLiveData<Resource<List<Category>>> categories = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<List<Category>>> activeCategories = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<Category>> category = new MediatorLiveData<>();

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository();
    }

    /**
     * Tải tất cả danh mục từ API
     * @return LiveData với Resource danh sách danh mục
     */
    public LiveData<Resource<List<Category>>> loadAllCategories() {
        if (!checkNetworkConnection()) {
            return categories;
        }

        LiveData<Resource<List<Category>>> source = categoryRepository.getAllCategories();
        categories.addSource(source, categories::setValue);
        return categories;
    }

    /**
     * Tải các danh mục đang hoạt động từ API
     * @return LiveData với Resource danh sách danh mục đang hoạt động
     */
    public LiveData<Resource<List<Category>>> loadActiveCategories() {
        if (!checkNetworkConnection()) {
            return activeCategories;
        }

        LiveData<Resource<List<Category>>> source = categoryRepository.getActiveCategories();
        activeCategories.addSource(source, activeCategories::setValue);
        return activeCategories;
    }

    /**
     * Tải một danh mục cụ thể theo ID
     * @param categoryId ID của danh mục cần tải
     * @return LiveData với Resource của danh mục
     */
    public LiveData<Resource<Category>> loadCategoryById(int categoryId) {
        if (!checkNetworkConnection()) {
            return category;
        }

        LiveData<Resource<Category>> source = categoryRepository.getCategoryById(categoryId);
        category.addSource(source, category::setValue);
        return category;
    }
    
    /**
     * Kiểm tra kết nối mạng và đặt trạng thái lỗi nếu không có kết nối
     * @return true nếu có kết nối mạng, false nếu không
     */
    private boolean checkNetworkConnection() {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            String errorMsg = "No internet connection. Please check and try again.";
            categories.setValue(Resource.error(errorMsg, null));
            activeCategories.setValue(Resource.error(errorMsg, null));
            category.setValue(Resource.error(errorMsg, null));
            return false;
        }
        return true;
    }
}