package com.huy.QuizMe.ui.main.join;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.utils.ApiUtils;
//import com.huy.QuizMe.ui.main.room.WaitingRoomFragment;

import java.util.List;

public class JoinRoomFragment extends Fragment {
    private JoinRoomViewModel viewModel;
    private RoomAdapter roomAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvRooms;
    private ChipGroup chipGroupCategories;
    private SearchView searchView;
    private View emptyView;
    private ExtendedFloatingActionButton fabCreateRoom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(JoinRoomViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_room, container, false);

        // Khởi tạo các thành phần UI
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        rvRooms = view.findViewById(R.id.rv_rooms);
        chipGroupCategories = view.findViewById(R.id.chip_group_categories);
        searchView = view.findViewById(R.id.search_view);
        emptyView = view.findViewById(R.id.empty_view);
        fabCreateRoom = view.findViewById(R.id.fab_create_room);

        setupSwipeRefresh();
        setupRecyclerView();
        setupSearch();
        setupFabCreateRoom();
//        loadCategories();
        loadRooms();

        return view;
    }

    // Thiết lập sự kiện click cho FAB tạo phòng
    private void setupFabCreateRoom() {
        fabCreateRoom.setOnClickListener(v -> {
            // Mở fragment tạo phòng mới
            navigateToCreateRoomFragment();
        });
    }

    // Chuyển đến fragment tạo phòng mới
    private void navigateToCreateRoomFragment() {
        CreateRoomFragment createRoomFragment = new CreateRoomFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frm_container, createRoomFragment)
                .addToBackStack(null)
                .commit();
    }

    // Hàm này sẽ được gọi khi người dùng kéo xuống để làm mới danh sách
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.purple);
        swipeRefreshLayout.setOnRefreshListener(this::loadRooms);
    }

    // Hàm này sẽ được gọi khi người dùng nhấn vào một phòng trong danh sách
    private void setupRecyclerView() {
        roomAdapter = new RoomAdapter(requireContext());
        rvRooms.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRooms.setAdapter(roomAdapter);        // Thiết lập sự kiện click phòng
        roomAdapter.setOnRoomClickListener(room -> {
            // Điều hướng tới màn hình phòng đợi
            navigateToWaitingRoom(room);
        });
    }

    // Tìm kiếm danh sách phòng
    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
//                    applyFilters();
                }
                return true;
            }
        });
    }

    // Tải danh sách các phòng
    private void loadRooms() {
        viewModel.getAvailableRooms().observe(getViewLifecycleOwner(), resource -> {
            if (ApiUtils.isLoading(resource)) {
                swipeRefreshLayout.setRefreshing(true);
                emptyView.setVisibility(View.GONE);
            } else if (ApiUtils.isSuccess(resource)) {
                swipeRefreshLayout.setRefreshing(false);
                List<Room> rooms = resource.getData();

                if (rooms != null && !rooms.isEmpty()) {
                    roomAdapter.updateItems(rooms);
                    rvRooms.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                } else {
                    rvRooms.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Chuyển đến fragment phòng đợi khi người dùng chọn một phòng
    private void navigateToWaitingRoom(Room room) {
//        // Tạo instance của WaitingRoomFragment
//        WaitingRoomFragment waitingRoomFragment = WaitingRoomFragment.newInstance(room);
//
//        // Hiển thị hộp thoại loading
//        // TODO: Hiển thị loading nếu cần
//
//        // Gọi API để tham gia phòng (nếu cần)
//        viewModel.joinRoom(room.getId()).observe(getViewLifecycleOwner(), resource -> {
//            // TODO: Ẩn loading nếu đã hiển thị
//
//            if (resource.getStatus() == Resource.Status.SUCCESS) {
//                // Điều hướng đến fragment phòng đợi
//                getParentFragmentManager().beginTransaction()
//                        .replace(R.id.frm_container, waitingRoomFragment)
//                        .addToBackStack(null)
//                        .commit();
//            } else {
//                // Hiển thị thông báo lỗi nếu không thể tham gia phòng
//                Toast.makeText(getContext(), "Không thể tham gia phòng: " +
//                    resource.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

}