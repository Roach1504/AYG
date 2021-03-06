package com.example.android.normalnotdagger.ui.message.list_dialog;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.normalnotdagger.R;
import com.example.android.normalnotdagger.api.ServiseMenager;
import com.example.android.normalnotdagger.models.new_model.messages.Message;
import com.example.android.normalnotdagger.ui.message.dialog_item.DialogItemFragment;

import java.util.ArrayList;
import java.util.List;


public class MessagesFragment extends Fragment implements MessagersMVP{
    List<Message> posts = new ArrayList<>();
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    MessagesPresentr pr;
    SharedPreferences user;
    ProgressDialog loading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        user = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        if(!user.getString("id","null").equals("null")) {
            ServiseMenager.getInstance().setMvp(this);
            ServiseMenager.getInstance().setMvpList(null);
            pr = new MessagesPresentr(this);
            loading = new ProgressDialog(getActivity());
            loading.setMessage("Загрузка сообщений");
            loading.setIndeterminate(true);
            loading.setCancelable(false);
            loading.show();
            pr.loadMessage(user.getString("id", "error"));

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
           // recyclerView.setAdapter(messageAdapter);
           // messageAdapter = new MessageAdapter(posts);
           // recyclerView.setAdapter(messageAdapter);

        }
        else{
            Toast.makeText(getActivity(),"Для просмотра сообщений необходимо авторезироватся",Toast.LENGTH_SHORT).show();
        }
        return recyclerView;
    }

    @Override
    public void creadDialogs(List<Message> list) {
        Log.e("mes", "OK");
        messageAdapter = new MessageAdapter(list ,this, user);
        recyclerView.setAdapter(messageAdapter);
    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void messagIsEmpty() {
        Toast.makeText(getActivity(),"У вас нет ни одного диалога",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startDiolog() {
        DialogItemFragment youFragment = new DialogItemFragment();
    android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()          // получаем экземпляр FragmentTransaction
                .replace(R.id.content, youFragment)
                .addToBackStack("myStack")
                .commit();
    }

    @Override
    public void stopProgressBar() {
        loading.dismiss();

    }

    @Override
    public void reset() {
        MessagesFragment youFragment = new MessagesFragment();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()          // получаем экземпляр FragmentTransaction
                .replace(R.id.news_list, youFragment)
                .addToBackStack("myStack")
                .commit();
    }
}
