package app.nepaliapp.sabbaikomaster.fragments.subjectFragments.tabLayoutFragments;

import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import app.nepaliapp.sabbaikomaster.R;

public class DescriptionFragment extends Fragment {
    TextView shortDescription, longDescription;

    public DescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        shortDescription = view.findViewById(R.id.textofShortDescription);
        longDescription = view.findViewById(R.id.textofLongDescription);

        if (getArguments() != null) {
            String shortDescriptionTxt = getArguments().getString("shortDescription", "No data available.");
            String longDescriptionTxt = getArguments().getString("longDescription", "No data available.");
            longDescriptionTxt = longDescriptionTxt.replace("<br>", "\n");
            Log.d("longDecription", longDescriptionTxt);
            shortDescription.setText(shortDescriptionTxt);
          // new method
            longDescriptionTxt = parseStrongTags(longDescriptionTxt);
            longDescription.setText(Html.fromHtml(longDescriptionTxt, Html.FROM_HTML_MODE_COMPACT));
        }

// Inflate the layout for this fragment
        return view;
    }
    private String parseStrongTags(String html) {
        String processedHtml = html.replaceAll("<strong>", "<b>").replaceAll("</strong>", "</b>");
        return processedHtml;
    }

    private String applyBoldTags(String html) {
        SpannableString spannableString = new SpannableString(html);

        // Manually find <strong> and apply bold style
        int start = html.indexOf("<strong>");
        while (start != -1) {
            int end = html.indexOf("</strong>", start);
            if (end != -1) {
                spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = html.indexOf("<strong>", end);
            } else {
                break;
            }
        }
        return spannableString.toString(); // Return modified spannable string as text
    }



    // Method to update the description dynamically
    public void updateDescription(String shortDescription, String longDescriptions) {
        if (this.shortDescription != null) {
            this.shortDescription.setText(shortDescription);
        }

        if (longDescription != null) {
            String longDescriptionTxt = longDescriptions.replace("<br>", "\n");
            longDescriptionTxt = parseStrongTags(longDescriptionTxt);
            longDescription.setText(Html.fromHtml(longDescriptionTxt, Html.FROM_HTML_MODE_COMPACT));
        }
    }
}