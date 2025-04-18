package com.developer.opdmanager.Activities;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.app.ActivityCompat.recreate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.developer.opdmanager.R;
import com.developer.opdmanager.Utils.LocaleHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profile_section#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile_section extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public profile_section() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment thirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profile_section newInstance(String param1, String param2) {
        profile_section fragment = new profile_section();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String lang = prefs.getString("lang", "default");
        if (!lang.equals("default")) {
            LocaleHelper.setLocale(getActivity(), lang);
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_section, container, false);

        Button languageButton = rootView.findViewById(R.id.languageButton);
        languageButton.setOnClickListener(v -> showLanguageDialog());
        return rootView;
    }
    private void showLanguageDialog() {
        String[] languages = {"English", "हिंदी","Tamil","Marathi", "System Default"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Language");

        builder.setItems(languages, (dialog, which) -> {
            String selectedLang;
            if (which == 0) selectedLang = "en";
            else if (which == 1) selectedLang = "hi";
            else if (which == 2) selectedLang = "ta";
            else if (which == 3) selectedLang = "mr";
            else selectedLang = "default";

            SharedPreferences prefs = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);
            String currentLang = prefs.getString("lang", "default");

            if (!selectedLang.equals(currentLang)) {
                prefs.edit().putString("lang", selectedLang).apply();

                if (!selectedLang.equals("default")) {
                    LocaleHelper.setLocale(getActivity(), selectedLang);
                }

                recreate(getActivity()); // Restart to apply language
            }
        });

        builder.show();
    }
}