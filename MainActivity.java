package pk.valuetrade.marketplace;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout list;
    EditText search;
    final String[] categories = {"All", "Mobiles", "Electronics", "Furniture", "Vehicles", "Home & Garden"};
    final ArrayList<Listing> listings = new ArrayList<>();
    final int navy = Color.rgb(20, 48, 73);
    final int teal = Color.rgb(0, 137, 123);

    static class Listing {
        String title, category, price, detail;
        Listing(String t, String c, String p, String d) { title=t; category=c; price=p; detail=d; }
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        listings.add(new Listing("Example: Used Android Phone", "Mobiles", "Rs. 18,000", "Demo listing — add your own details."));
        listings.add(new Listing("Example: Office Chair", "Furniture", "Rs. 4,500", "Demo listing — contact seller to enquire."));
        listings.add(new Listing("Example: LED TV", "Electronics", "Rs. 22,000", "Demo listing — sample only."));
        buildScreen();
    }

    void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 18, 18, 24); root.setBackgroundColor(Color.rgb(246,249,251));
        scroll.addView(root);

        TextView brand = new TextView(this); brand.setText("ValueTrade"); brand.setTextSize(30); brand.setTextColor(navy); brand.setTypeface(null, 1);
        root.addView(brand);
        TextView tagline = new TextView(this); tagline.setText("Karachi's local buy & sell marketplace"); tagline.setTextSize(14); tagline.setTextColor(Color.DKGRAY);
        root.addView(tagline);

        Button sell = button("＋  Post an item for sale"); sell.setOnClickListener(v -> showSellForm()); root.addView(sell);
        search = new EditText(this); search.setSingleLine(true); search.setHint("Search items, e.g. mobile, chair…");
        root.addView(search);
        LinearLayout chips = new LinearLayout(this); chips.setOrientation(LinearLayout.HORIZONTAL);
        for (String c : categories) {
            Button chip = new Button(this); chip.setText(c); chip.setTextSize(10); chip.setPadding(4,0,4,0);
            chip.setOnClickListener(v -> { search.setText(c.equals("All") ? "" : c); }); chips.addView(chip);
        }
        root.addView(chips);
        TextView heading = new TextView(this); heading.setText("Browse listings"); heading.setTextSize(21); heading.setTextColor(navy); heading.setPadding(0,18,0,8); root.addView(heading);
        list = new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); root.addView(list);
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int st,int count,int after){}
            public void onTextChanged(CharSequence s,int st,int before,int count){ renderListings(s.toString()); }
            public void afterTextChanged(Editable e){}
        });
        renderListings("");
        TextView contact = new TextView(this);
        contact.setText("\nNeed help? Contact ValueTrade\nOwner: S. Ghazanfar Hashmi\n0333-6890790  |  0315-6890790");
        contact.setTextSize(15); contact.setTextColor(navy); contact.setGravity(Gravity.CENTER); root.addView(contact);
        Button whatsapp = button("Chat on WhatsApp"); whatsapp.setOnClickListener(v -> openWhatsApp("923336890790")); root.addView(whatsapp);
        setContentView(scroll);
    }

    Button button(String text) {
        Button b = new Button(this); b.setText(text); b.setTextColor(Color.WHITE); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(teal)); return b;
    }

    void renderListings(String query) {
        if (list == null) return;
        list.removeAllViews();
        boolean found = false;
        for (Listing item : listings) {
            String q = query.toLowerCase(Locale.ROOT).trim();
            if (!q.isEmpty() && !item.title.toLowerCase(Locale.ROOT).contains(q) && !item.category.toLowerCase(Locale.ROOT).contains(q) && !item.detail.toLowerCase(Locale.ROOT).contains(q)) continue;
            found = true;
            LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(14,12,14,12);
            card.setBackgroundColor(Color.WHITE);
            TextView title = new TextView(this); title.setText(item.title); title.setTextSize(18); title.setTextColor(navy); title.setTypeface(null,1); card.addView(title);
            TextView price = new TextView(this); price.setText(item.price + "  •  " + item.category); price.setTextSize(16); price.setTextColor(teal); card.addView(price);
            TextView detail = new TextView(this); detail.setText(item.detail); detail.setTextColor(Color.DKGRAY); card.addView(detail);
            Button contact = new Button(this); contact.setText("Enquire on WhatsApp"); contact.setOnClickListener(v -> openWhatsApp("923336890790")); card.addView(contact);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.setMargins(0,0,0,12); list.addView(card, lp);
        }
        if (!found) { TextView empty = new TextView(this); empty.setText("No matching listings. Try another search."); list.addView(empty); }
    }

    void showSellForm() {
        LinearLayout form = new LinearLayout(this); form.setOrientation(LinearLayout.VERTICAL); form.setPadding(24,24,24,24);
        EditText title = field("Item name"); EditText category = field("Category"); EditText price = field("Price (PKR)"); EditText details = field("Description / area");
        form.addView(title); form.addView(category); form.addView(price); form.addView(details);
        new AlertDialog.Builder(this).setTitle("Post an item").setView(form)
            .setPositiveButton("Prepare WhatsApp Ad", (d,w) -> {
                String message = "ValueTrade listing\nItem: "+title.getText()+"\nCategory: "+category.getText()+"\nPrice: Rs. "+price.getText()+"\nDetails: "+details.getText();
                openWhatsAppWithMessage("923336890790", message);
            }).setNegativeButton("Cancel", null).show();
    }

    EditText field(String hint) { EditText e = new EditText(this); e.setHint(hint); return e; }
    void openWhatsApp(String phone) { openWhatsAppWithMessage(phone, "Assalam-o-Alaikum, I am contacting you about ValueTrade."); }
    void openWhatsAppWithMessage(String phone, String message) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/"+phone+"?text="+Uri.encode(message)));
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp could not be opened.", Toast.LENGTH_LONG).show();
        }
    }
}
