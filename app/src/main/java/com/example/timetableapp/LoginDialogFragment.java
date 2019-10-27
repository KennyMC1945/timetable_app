package com.example.timetableapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginDialogFragment extends DialogFragment implements View.OnClickListener {

    private final String LOGIN_URL = "/login";
    private EditText et_email;
    private EditText et_pass;
    //private ConnectionManager cm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_login,null);
        et_email = view.findViewById(R.id.dialog_login_et_email);
        et_pass = view.findViewById(R.id.dialog_login_et_pass);
        view.findViewById(R.id.dialog_login_btn_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_login_btn_signIn).setOnClickListener(this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_login_btn_cancel:
                dismiss();
                break;
            case R.id.dialog_login_btn_signIn:
                ConnectionManager cm = new ConnectionManager(getString(R.string.server_url),getContext());
                try {
                    JSONObject params = new JSONObject();
                    params.put("mail",et_email.getText().toString());
                    params.put("pass",et_pass.getText().toString());
                    cm.postJSONRequest(LOGIN_URL, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int status = response.getInt("status");
                                if (status == 200) {
                                    Toast.makeText(getContext(),"Success!\nToken: " + response.getString("token"),Toast.LENGTH_LONG);
                                } else if (status == 400) {
                                    Toast.makeText(getContext(),"Failed!",Toast.LENGTH_SHORT);
                                }
                            } catch (JSONException e) {}
                        }
                    });
                } catch (JSONException e) {}
                break;
        }
    }
}
