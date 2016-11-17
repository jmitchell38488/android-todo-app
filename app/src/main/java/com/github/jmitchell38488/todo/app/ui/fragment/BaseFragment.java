package com.github.jmitchell38488.todo.app.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private Unbinder unbinder;
    private Toast mToast;

    @CallSuper
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void showToast(String message) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    protected void showToast(@StringRes int resId) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT);
        mToast.show();
    }

}
