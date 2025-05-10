package com.huy.QuizMe.ui.main.join;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.data.repository.RoomRepository;

import java.util.List;

public class JoinRoomViewModel extends ViewModel {
    private final RoomRepository roomRepository;

    // Biến lưu trạng thái lọc hiển tại
    private Long currentCategoryId = null;
    private String currentSearch = null;

    public JoinRoomViewModel() {
        roomRepository = new RoomRepository();
    }

    public void setFilters(Long categoryId, String search) {
        this.currentCategoryId = categoryId;
        this.currentSearch = search;
    }

    // Lấy danh sách phòng chơi từ repository
    public LiveData<Resource<List<Room>>> getAvailableRooms() {
        return roomRepository.getAvailableRooms(currentCategoryId, currentSearch);
    }

    // Gọi API để tham gia một phòng
    public LiveData<Resource<Room>> joinRoom(Long roomId) {
        return roomRepository.joinRoom(roomId);
    }
}