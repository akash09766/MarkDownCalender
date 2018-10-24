package com.developer.skylight.markdowncalender.calender;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.developer.skylight.markdowncalender.R;
import com.developer.skylight.markdowncalender.model.CalenderData;
import com.developer.skylight.markdowncalender.model.ResponseData;
import com.developer.skylight.markdowncalender.util.Constants;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CustomCalendarView extends LinearLayout {
    private Context mContext;

    private View view;
    private ImageView previousMonthButton;
    private ImageView nextMonthButton;

    private CalendarListener calendarListener;
    private Calendar currentCalendar;
    private Locale locale;

    private Date lastSelectedDay;
    private Typeface customTypeface;
    float x1, x2;
    float y1, y2;
    private int firstDayOfWeek = Calendar.MONDAY;
    int pickMaxLine, dropMaxLine;
    private List<DayDecorator> decorators = null;

    private static final String DAY_OF_WEEK = "dayOfWeek";
    private static final String DAY_OF_MONTH_TEXT = "dayOfMonthText";
    private static final String DAY_OF_MONTH_CONTAINER = "dayOfMonthContainer";
    private Fragment mActivity;
    private int disabledDayBackgroundColor;
    private int disabledDayTextColor;

    private int calendarBackgroundColor;
    private int selectedDayBackground;
    private int weekLayoutBackgroundColor;
    private int calendarTitleBackgroundColor;
    private int selectedDayTextColor;
    private int calendarTitleTextColor;
    private int dayOfWeekTextColor;
    private int dayOfMonthTextColor;
    private int currentDayOfMonth;
    private int weekEndColor;
    private int dayColor;
    private int currentMonthIndex = 0;
    private boolean isOverflowDateVisible = true;
    JsonObjectRequest getRequest;
    SharedPreferences sp;
    ArrayList<String> dayStatusArray = new ArrayList<String>();
    ArrayList<String> dateArray = new ArrayList<String>();
    HashMap<Integer, Integer> colorMap = new HashMap<>();
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, Integer>> weeklyoffMap = new ArrayList<HashMap<String, Integer>>();
    // ArrayList<Integer> colorArray = new ArrayList<Integer>();
    public TypedArray typedArray;
    private String TAG = CustomCalendarView.class.getSimpleName();

    public CustomCalendarView(Context mContext) {
        this(mContext, null);
    }

    public CustomCalendarView(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        this.mContext = mContext;
        sp = mContext.getSharedPreferences("LoginPrefs", 0);

        colorMap.put(Constants.PRESENT, R.color.present);
        colorMap.put(Constants.ABSENT, R.color.absent);
        colorMap.put(Constants.HOLIDAY, R.color.holiday);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            if (isInEditMode())
                return;
        }

        getAttributes(attrs);

        initializeCalendar();
        // pickTimeTextviewHeight= RosterCalendar.pickTimeTextview.getHeight();
        //  dropTimeTextviewHeight= RosterCalendar.dropTimeTextview.getHeight();
    }

    private void getAttributes(AttributeSet attrs) {
        typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CustomCalendarView, 0, 0);
        calendarBackgroundColor = typedArray.getColor(R.styleable.CustomCalendarView_calendarBackgroundColor, getResources().getColor(R.color.white));
        calendarTitleBackgroundColor = typedArray.getColor(R.styleable.CustomCalendarView_titleLayoutBackgroundColor, getResources().getColor(R.color.white));
        calendarTitleTextColor = typedArray.getColor(R.styleable.CustomCalendarView_calendarTitleTextColor, getResources().getColor(R.color.white));
        weekLayoutBackgroundColor = typedArray.getColor(R.styleable.CustomCalendarView_weekLayoutBackgroundColor, getResources().getColor(R.color.white));
        dayOfWeekTextColor = typedArray.getColor(R.styleable.CustomCalendarView_dayOfWeekTextColor, getResources().getColor(R.color.black));
        dayOfMonthTextColor = typedArray.getColor(R.styleable.CustomCalendarView_dayOfMonthTextColor, getResources().getColor(R.color.black));
        weekEndColor = typedArray.getColor(R.styleable.CustomCalendarView_dayOfMonthTextColor, getResources().getColor(R.color.red));
        disabledDayBackgroundColor = typedArray.getColor(R.styleable.CustomCalendarView_disabledDayBackgroundColor, getResources().getColor(R.color.white));
        disabledDayTextColor = typedArray.getColor(R.styleable.CustomCalendarView_disabledDayTextColor, getResources().getColor(R.color.day_disabled_text_color));
        selectedDayBackground = typedArray.getColor(R.styleable.CustomCalendarView_selectedDayBackgroundColor, getResources().getColor(R.color.selected_day_background));
        selectedDayTextColor = typedArray.getColor(R.styleable.CustomCalendarView_selectedDayTextColor, getResources().getColor(R.color.white));
        currentDayOfMonth = typedArray.getColor(R.styleable.CustomCalendarView_currentDayOfMonthColor, getResources().getColor(R.color.current_day_of_month));

        typedArray.recycle();

    }

    private void initializeCalendar() {
        final LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflate.inflate(R.layout.custom_calendar_layout, this, true);

        previousMonthButton = (ImageView) view.findViewById(R.id.leftButton);
        nextMonthButton = (ImageView) view.findViewById(R.id.rightButton);

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    // when user first touches the screen we get x and y coordinate
                    case MotionEvent.ACTION_DOWN: {
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        x2 = event.getX();
                        y2 = event.getY();

                        //if left to right sweep event on screen
                        if (x1 < x2) {
                            // Toast.makeText(this, "Left to Right Swap Performed", Toast.LENGTH_LONG).show();
                            currentMonthIndex--;
                            currentCalendar = Calendar.getInstance(Locale.getDefault());
                            currentCalendar.add(Calendar.MONTH, currentMonthIndex);
                            int currentYear = currentCalendar.get(Calendar.YEAR);
                            int currentMonth = currentCalendar.get(Calendar.MONTH) + 1;
                            getServiceData(0, currentMonth, currentYear);
                            // refreshCalendar(currentCalendar);
                            if (calendarListener != null) {
                                calendarListener.onMonthChanged(currentCalendar.getTime());
                            }
                        }
                        // if right to left sweep event on screen
                        if (x1 > x2) {
                            currentMonthIndex++;
                            currentCalendar = Calendar.getInstance(Locale.getDefault());
                            currentCalendar.add(Calendar.MONTH, currentMonthIndex);
                            int currentYear = currentCalendar.get(Calendar.YEAR);
                            int currentMonth = currentCalendar.get(Calendar.MONTH) + 1;
                            getServiceData(0, currentMonth, currentYear);
                            // refreshCalendar(currentCalendar);

                            if (calendarListener != null) {
                                calendarListener.onMonthChanged(currentCalendar.getTime());
                            }

                            // Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_LONG).show();
                        }


                        break;
                    }
                }
                return true;
            }
        });


        previousMonthButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //  RosterCalendar.pickTimeTextview.getLayoutParams().height=RosterCalendar.pickTimeTextviewHeight;
                //  RosterCalendar.dropTimeTextview.getLayoutParams().height=RosterCalendar.dropTimeTextviewHeight;
                currentMonthIndex--;
                currentCalendar = Calendar.getInstance(Locale.getDefault());
                currentCalendar.add(Calendar.MONTH, currentMonthIndex);
                int currentYear = currentCalendar.get(Calendar.YEAR);
                int currentMonth = currentCalendar.get(Calendar.MONTH) + 1;
                getServiceData(0, currentMonth, currentYear);
                // refreshCalendar(currentCalendar);
                if (calendarListener != null) {
                    calendarListener.onMonthChanged(currentCalendar.getTime());
                }

            }
        });

        nextMonthButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // RosterCalendar.pickTimeTextview.getLayoutParams().height=RosterCalendar.pickTimeTextviewHeight;
                // RosterCalendar.dropTimeTextview.getLayoutParams().height=RosterCalendar.dropTimeTextviewHeight;
                currentMonthIndex++;
                currentCalendar = Calendar.getInstance(Locale.getDefault());
                currentCalendar.add(Calendar.MONTH, currentMonthIndex);
                int currentYear = currentCalendar.get(Calendar.YEAR);
                int currentMonth = currentCalendar.get(Calendar.MONTH) + 1;
                getServiceData(0, currentMonth, currentYear);
                // refreshCalendar(currentCalendar);

                if (calendarListener != null) {
                    calendarListener.onMonthChanged(currentCalendar.getTime());
                }
                // pickTimeTextviewHeight= RosterCalendar.pickTimeTextview.getHeight();
                // dropTimeTextviewHeight= RosterCalendar.dropTimeTextview.getHeight();
            }
        });

        // Initialize calendar for current month
        Locale locale = mContext.getResources().getConfiguration().locale;
        currentCalendar = Calendar.getInstance(locale);

        setFirstDayOfWeek(Calendar.MONDAY);

        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH) + 1;
        getServiceData(0, currentMonth, currentYear);

    }


    /**
     * Display calendar title with next previous month button
     */
    private void initializeTitleLayout() {
        View titleLayout = view.findViewById(R.id.titleLayout);
        titleLayout.setBackgroundResource(R.color.primaryColor);

        String dateText = new DateFormatSymbols(locale).getMonths()[currentCalendar.get(Calendar.MONTH)].toString();
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length());

        TextView dateTitle = (TextView) view.findViewById(R.id.dateTitle);
        dateTitle.setTextColor(calendarTitleTextColor);
        dateTitle.setText(dateText + " " + currentCalendar.get(Calendar.YEAR));
        dateTitle.setTextColor(calendarTitleTextColor);
        if (null != getCustomTypeface()) {
            dateTitle.setTypeface(getCustomTypeface(), Typeface.BOLD);
        }

    }

    /**
     * Initialize the calendar week layout, considers start day
     */
    @SuppressLint("DefaultLocale")
    private void initializeWeekLayout() {

        TextView dayOfWeek;
        String dayOfTheWeekString;

        //Setting background color white
        View titleLayout = view.findViewById(R.id.weekLayout);
        titleLayout.setBackgroundColor(weekLayoutBackgroundColor);

        final String[] weekDaysArray = new DateFormatSymbols(locale).getShortWeekdays();
        int j = 0;
        for (int i = 1; i < weekDaysArray.length; i++) {
            dayOfTheWeekString = weekDaysArray[i];
            // if(dayOfTheWeekString.length() > 3){
            dayOfTheWeekString = dayOfTheWeekString.substring(0, 3).toUpperCase();
            //}

            dayOfWeek = (TextView) view.findViewWithTag(DAY_OF_WEEK + getWeekIndex(i, currentCalendar));
            dayOfWeek.setText(dayOfTheWeekString);
           /* if (dayStatusArray.get(j).equals("1")) {
                dayOfWeek.setTextColor(weekEndColor);
            } else {
                dayOfWeek.setTextColor(dayOfWeekTextColor);
            }*/
            if (null != getCustomTypeface()) {
                dayOfWeek.setTypeface(getCustomTypeface());
            }
            j++;
        }
    }

    private void setDaysInCalendar() {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);

        // Calculate dayOfMonthIndex
        int dayOfMonthIndex = getWeekIndex(firstDayOfMonth, calendar);
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        final Calendar startCalendar = (Calendar) calendar.clone();
        //Add required number of days
        startCalendar.add(Calendar.DATE, -(dayOfMonthIndex - 1));
        int monthEndIndex = 42 - (actualMaximum + dayOfMonthIndex - 1);

        DayView dayView;
        ViewGroup dayOfMonthContainer;
        int j = 0;
        for (int i = 1; i < 43; i++) {
            dayOfMonthContainer = (ViewGroup) view.findViewWithTag(DAY_OF_MONTH_CONTAINER + i);
            dayView = (DayView) view.findViewWithTag(DAY_OF_MONTH_TEXT + i);
            // RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)dayView.getLayoutParams();
            // params.setMargins(10, 50, 10, 50);

            //  dayView.setLayoutParams(params);

            if (dayView == null)
                continue;

            //Apply the default styles
            dayOfMonthContainer.setOnClickListener(null);
            int weeklyOff = 0;
            int mapDay = 0;
            if (j < weeklyoffMap.size()) {
                weeklyOff = weeklyoffMap.get(j).get("woho");
                mapDay = weeklyoffMap.get(j).get("Day");
            }
            dayView.bind(startCalendar.getTime(), getDecorators(), weeklyOff, mapDay);

            dayView.setVisibility(View.VISIBLE);

            if (null != getCustomTypeface()) {
                dayView.setTypeface(getCustomTypeface());
            }


            if (isSameMonth(calendar, startCalendar)) {
                dayOfMonthContainer.setOnClickListener(onDayOfMonthClickListener);
                dayView.setBackgroundColor(calendarBackgroundColor);
                if (j < dayStatusArray.size()) {
                    // System.out.println("DayStatus****************" + dateArray.get(j) + " = " + dayStatusArray.get(j));
                    //if (!dayStatusArray.get(i).isEmpty() && dayStatusArray.get(i) != "") {
                    int status = Integer.parseInt(dayStatusArray.get(j));
                    //GradientDrawable gd = new GradientDrawable();
                    //   gd.setColor(getResources().getColor(colorArray.get(status))); // Changes this drawbale to use a single color instead of a gradient
                    // gd.setCornerRadius(50);
                    //   gd.setSize(20,20);
                    //    gd.setShape(GradientDrawable.OVAL);
                    // gd.setCornerRadius(50);

                    // gd.setStroke(1, 0xFF000000);

                    // dayView.setBackgroundResource(colorArray.get(status))


                    //lower semi circle
                    if (status == Constants.PRESENT) {
                        /*dayView.setBackgroundResource(R.drawable.lower_semicircle);
                        ClipDrawable bgShape = (ClipDrawable) dayView.getBackground();
                        bgShape.setColorFilter(getResources().getColor(colorMap.get(status)), PorterDuff.Mode.SRC_IN);
                        dayView.getBackground().setLevel(5000);*/

                        dayView.setBackgroundDrawable(new PercentView(getResources().getColor(R.color.present), getResources().getColor(R.color.present), dayView.getHeight(), dayView.getWidth()));
                    } else if (status == Constants.ABSENT) {
                        dayView.setBackgroundDrawable(new PercentView(getResources().getColor(R.color.absent), getResources().getColor(R.color.absent), dayView.getHeight(), dayView.getWidth()));

                    } else if (status == Constants.HOLIDAY) {
                        dayView.setBackgroundDrawable(new PercentView(getResources().getColor(R.color.holiday), getResources().getColor(R.color.holiday), dayView.getHeight(), dayView.getWidth()));
                    }
                }
                j++;

            } else {
                dayView.setBackgroundColor(disabledDayBackgroundColor);
                dayView.setTextColor(disabledDayTextColor);

                if (!isOverflowDateVisible())
                    dayView.setVisibility(View.GONE);
                else if (i >= 36 && ((float) monthEndIndex / 7.0f) >= 1) {
                    dayView.setVisibility(View.GONE);

                }
            }
            dayView.decorate();
            //Set the current day color
            markDayAsCurrentDay(startCalendar);

            startCalendar.add(Calendar.DATE, 1);
            dayOfMonthIndex++;

        }

        // If the last week row has no visible days, hide it or show it in case
        ViewGroup weekRow = (ViewGroup) view.findViewWithTag("weekRow6");
        dayView = (DayView) view.findViewWithTag("dayOfMonthText36");
        if (dayView.getVisibility() != VISIBLE) {
            weekRow.setVisibility(GONE);
        } else {
            weekRow.setVisibility(VISIBLE);
        }
    }


    public boolean isSameMonth(Calendar c1, Calendar c2) {
        if (c1 == null || c2 == null)
            return false;
        return (c1.get(Calendar.ERA) == c2.get(Calendar.ERA)
                && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
    }

    /**
     * <p>Checks if a calendar is today.</p>
     *
     * @param calendar the calendar, not altered, not null.
     * @return true if the calendar is today.
     * @throws IllegalArgumentException if the calendar is <code>null</code>
     */
    public static boolean isToday(Calendar calendar) {
        return isSameDay(calendar, Calendar.getInstance());
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null)
            throw new IllegalArgumentException("The dates must not be null");
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }


    private void clearDayOfTheMonthStyle(Date currentDate) {
        if (currentDate != null) {
            final Calendar calendar = getTodaysCalendar();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.setTime(currentDate);

            final DayView dayView = getDayOfMonthText(calendar);
            dayView.setBackgroundColor(calendarBackgroundColor);
            dayView.setTextColor(dayOfWeekTextColor);
        }
    }

    private DayView getDayOfMonthText(Calendar currentCalendar) {
        return (DayView) getView(DAY_OF_MONTH_TEXT, currentCalendar);
    }

    private int getDayIndexByDate(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int index = currentDay + monthOffset;
        return index;
    }

    private int getMonthOffset(Calendar currentCalendar) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayWeekPosition = calendar.getFirstDayOfWeek();
        int dayPosition = calendar.get(Calendar.DAY_OF_WEEK);

        if (firstDayWeekPosition == 1) {
            return dayPosition - 1;
        } else {
            if (dayPosition == 1) {
                return 6;
            } else {
                return dayPosition - 2;
            }
        }
    }

    private int getWeekIndex(int weekIndex, Calendar currentCalendar) {
        int firstDayWeekPosition = currentCalendar.getFirstDayOfWeek();
        if (firstDayWeekPosition == 1) {
            return weekIndex;
        } else {

            if (weekIndex == 1) {
                return 7;
            } else {
                return weekIndex - 1;
            }
        }
    }

    private View getView(String key, Calendar currentCalendar) {
        int index = getDayIndexByDate(currentCalendar);
        View childView = view.findViewWithTag(key + index);
        return childView;
    }

    private Calendar getTodaysCalendar() {
        Calendar currentCalendar = Calendar.getInstance(mContext.getResources().getConfiguration().locale);
        currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        return currentCalendar;
    }

    @SuppressLint("DefaultLocale")
    public void refreshCalendar(Calendar currentCalendar) {
        this.currentCalendar = currentCalendar;
        this.currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        locale = mContext.getResources().getConfiguration().locale;

        // Set date title
        initializeTitleLayout();

        // Set weeks days titles
        initializeWeekLayout();

        // Initialize and set days in calendar
        setDaysInCalendar();


    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public void markDayAsCurrentDay(Calendar calendar) {
        if (calendar != null && isToday(calendar)) {
            DayView dayOfMonth = getDayOfMonthText(calendar);
            dayOfMonth.setTextColor(currentDayOfMonth);
        }
    }

    /*public void markDayAsSelectedDay(Date currentDate) {
        final Calendar currentCalendar = getTodaysCalendar();
        currentCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        currentCalendar.setTime(currentDate);

        // Clear previous marks
        clearDayOfTheMonthStyle(lastSelectedDay);

        // Store current values as last values
        storeLastValues(currentDate);

        // Mark current day as selected
        DayView view = getDayOfMonthText(currentCalendar);

        view.setBackgroundResource(R.drawable.bg_red);
        view.setTextColor(selectedDayTextColor);
    }*/

    private void storeLastValues(Date currentDate) {
        lastSelectedDay = currentDate;
    }

    public void setCalendarListener(CalendarListener calendarListener) {
        this.calendarListener = calendarListener;
    }

    private OnClickListener onDayOfMonthClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            ViewGroup dayOfMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfMonthContainer.getTag();
            tagId = tagId.substring(DAY_OF_MONTH_CONTAINER.length(), tagId.length());
            final TextView dayOfMonthText = (TextView) view.findViewWithTag(DAY_OF_MONTH_TEXT + tagId);

            showToast(dayOfMonthText.getText().toString());
            Log.d(TAG, "onClick: dayOfMonthText : " + dayOfMonthText.getText().toString());

            // Extract day selected
            /*ViewGroup dayOfMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfMonthContainer.getTag();
            tagId = tagId.substring(DAY_OF_MONTH_CONTAINER.length(), tagId.length());
            final TextView dayOfMonthText = (TextView) view.findViewWithTag(DAY_OF_MONTH_TEXT + tagId);
            // Fire event
            final Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.setTime(currentCalendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayOfMonthText.getText().toString()));
            //  markDayAsSelectedDay(calendar.getTime());

            //Set the current day color
            //    markDayAsCurrentDay(currentCalendar);
            int selectedDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
            int selectedYear = currentCalendar.get(Calendar.YEAR);
            int selectedMonth = currentCalendar.get(Calendar.MONTH) + 1;
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
            String selectedDate = df.format(calendar.getTime());

            getDateDetails(dayOfMonthText.getText().toString(), selectedMonth, selectedYear, selectedDate);
            //getDateDetails(dayOfMonthText.getText().toString(), currentCalendar);
            if (calendarListener != null)
                calendarListener.onDateSelected(calendar.getTime());*/
        }
    };

    private void showToast(String msg) {

        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public List<DayDecorator> getDecorators() {
        return decorators;
    }

    public void setDecorators(List<DayDecorator> decorators) {
        this.decorators = decorators;
    }

    public boolean isOverflowDateVisible() {
        return isOverflowDateVisible;
    }

    public void setShowOverflowDate(boolean isOverFlowEnabled) {
        isOverflowDateVisible = isOverFlowEnabled;
    }

    public void setCustomTypeface(Typeface customTypeface) {
        this.customTypeface = customTypeface;
    }

    public Typeface getCustomTypeface() {
        return customTypeface;
    }

    public Calendar getCurrentCalendar() {
        return currentCalendar;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getResources().getAssets().open("CalenderJsonData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.e(TAG, "loadJSONFromAsset: IOException : " + ex.getMessage());
            return null;
        }
        return json;
    }

    public void getServiceData(int day, final int month, int year) {

        String jsonData = loadJSONFromAsset();

        ResponseData response = new Gson().fromJson(jsonData, ResponseData.class);

        dayStatusArray.clear();
        weeklyoffMap.clear();
        dateArray.clear();

        for (CalenderData item : response.getCalenderData()) {
            HashMap<String, Integer> hashMap = new HashMap<>();

            dayStatusArray.add(String.valueOf(item.getStatusCode()));

            hashMap.put("Day", item.getDay());
            hashMap.put("woho", 0);

            weeklyoffMap.add(hashMap);
            dateArray.add(String.valueOf(item.getDay()));
        }

        refreshCalendar(currentCalendar);
    }

    public Fragment getmActivity() {
        return mActivity;
    }

    /**
     * @param mActivity of type FragmentActivity
     * @return of type null
     * setter function for mActivity
     * @author rajeshcp
     * @since May 3, 2013
     */
    public void setmActivity(Fragment mActivity) {
        this.mActivity = mActivity;
        mActivity.getFragmentManager();
    }
    public Date getDate(int i) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, i);
        int endDay = cal.get(Calendar.DAY_OF_MONTH);
        try {

            date = df.parse(df.format(cal.getTime()));
        } catch (Exception e) {

        }
        return date;
    }

    public String removeDuplicateString(String s) {
        List<String> values = new ArrayList<String>();
        String[] splitted = s.split(",");
        Arrays.sort(splitted);
        StringBuilder sb = new StringBuilder();

        // java.util.Collections.sort(arrayList,icc);
        for (int i = 0; i < splitted.length; ++i) {
            if (!splitted[i].equals(" ") &&
                    !splitted[i].isEmpty() && splitted[i] != null && !values.contains(splitted[i])) {
                values.add(splitted[i]);
                sb.append(' ').append('/');
                sb.append(splitted[i]);
            }
        }
        // stringSort(sb.substring(1));
        return sb.substring(1);

    }


}


