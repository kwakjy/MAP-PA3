package edu.skku.cs.pa3.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import edu.skku.cs.pa3.R;
import edu.skku.cs.pa3.adapter.RecipeGridAdapter;
import edu.skku.cs.pa3.contract.SearchContract;
import edu.skku.cs.pa3.presenter.SearchPresenter;

public class SearchFragment extends Fragment implements SearchContract.View {
    private GridView recipeGridView;
    private SearchContract.Presenter presenter;
    private RecipeGridAdapter recipeGridAdapter;
    private EditText editText;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        recipeGridView = rootView.findViewById(R.id.gridView);
        recipeGridView.setEmptyView(rootView.findViewById(R.id.noItemTextView));
        recipeGridAdapter = new RecipeGridAdapter();
        recipeGridView.setAdapter(recipeGridAdapter);

        String email = getArguments().getString("email");
        Log.i("test", "sf - email: " + email);

        recipeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("id", recipeGridAdapter.getItem(i).getId());
                intent.putExtra("title", recipeGridAdapter.getItem(i).getTitle());
                intent.putExtra("image", recipeGridAdapter.getItem(i).getImage());
                intent.putExtra("from", "search");

                startActivity(intent);
            }
        });

        presenter = new SearchPresenter(SearchFragment.this);
        presenter.setAdapterModel(recipeGridAdapter);
        presenter.setAdapterView(recipeGridAdapter);

        editText = rootView.findViewById(R.id.editText);
        editText.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER) {
                presenter.loadName(editText.getText().toString());
                presenter.searchRecipes();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                return true;
            }
            else {
                return false;
            }
        });

        return rootView;
    }
}