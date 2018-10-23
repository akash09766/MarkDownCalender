/***********************************************************************************
 * The MIT License (MIT)

 * Copyright (c) 2014 Robin Chutaux

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ***********************************************************************************/
package com.developer.skylight.markdowncalender.calender;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.developer.skylight.markdowncalender.R;


import java.security.InvalidParameterException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

class SimpleMonthView extends View {
	public static int flag = 0;
	public static final String VIEW_PARAMS_HEIGHT = "height";
	public static final String VIEW_PARAMS_MONTH = "month";
	public static final String VIEW_PARAMS_YEAR = "year";
	public static final String VIEW_PARAMS_SELECTED_BEGIN_DAY = "selected_begin_day";
	public static final String VIEW_PARAMS_SELECTED_LAST_DAY = "selected_last_day";
	public static final String VIEW_PARAMS_SELECTED_BEGIN_MONTH = "selected_begin_month";
	public static final String VIEW_PARAMS_SELECTED_LAST_MONTH = "selected_last_month";
	public static final String VIEW_PARAMS_SELECTED_BEGIN_YEAR = "selected_begin_year";
	public static final String VIEW_PARAMS_SELECTED_LAST_YEAR = "selected_last_year";
	public static final String VIEW_PARAMS_WEEK_START = "week_start";

	private static final int SELECTED_CIRCLE_ALPHA = 128;
	protected static int DEFAULT_HEIGHT = 32;
	protected static final int DEFAULT_NUM_ROWS = 6;
	protected static int DAY_SELECTED_CIRCLE_SIZE;
	protected static int DAY_SEPARATOR_WIDTH = 1;
	protected static int MINI_DAY_NUMBER_TEXT_SIZE;
	protected static int MIN_HEIGHT = 10;
	protected static int MONTH_DAY_LABEL_TEXT_SIZE;
	protected static int MONTH_HEADER_SIZE;
	protected static int MONTH_LABEL_TEXT_SIZE;

	protected int mPadding = 0;

	private String mDayOfWeekTypeface;
	private String mMonthTitleTypeface;

	protected Paint mMonthDayLabelPaint;
	protected Paint mMonthNumPaint;
	protected Paint mMonthTitleBGPaint;
	protected Paint mMonthTitlePaint;
	protected Paint mSelectedCirclePaint;
	protected int mCurrentDayTextColor;
	protected int mMonthTextColor;
	protected int mDayTextColor;
	protected int mDayWeekEndColor;
	protected int mDayNumColor;
	protected int mMonthTitleBGColor;
	protected int mPreviousDayColor;
	protected int mSelectedDaysColor;

	private final StringBuilder mStringBuilder;

	protected boolean mHasToday = false;
	protected boolean mIsPrev = false;
	protected int mSelectedBeginDay = -1;
	protected int mSelectedLastDay = -1;
	protected int mSelectedBeginMonth = -1;
	protected int mSelectedLastMonth = -1;
	protected int mSelectedBeginYear = -1;
	protected int mSelectedLastYear = -1;
	protected int mToday = -1;
	protected int mWeekStart = 1;
	protected int mNumDays = 7;
	protected int mNumCells = mNumDays;
	private int mDayOfWeekStart = 0;
	protected int mMonth;
	protected Boolean mDrawRect;
	protected int mRowHeight = DEFAULT_HEIGHT;
	protected int mWidth, disableFlag;
	protected int mYear;
	final Time today;

	private final Calendar mCalendar;
	private final Calendar mDayLabelCalendar;
	private final Boolean isPrevDayEnabled;

	private int mNumRows = DEFAULT_NUM_ROWS;
	SharedPreferences sp;
	private DateFormatSymbols mDateFormatSymbols = new DateFormatSymbols();
	Resources resources;
	private OnDayClickListener mOnDayClickListener;
	TypedArray typedArray;
	String disableMonth, disableYear, disableFromDay, prvDisableMonth,
			prvDisableYear, prvDisableFromDay;;

	public SimpleMonthView(Context context, TypedArray typedArray2) {
		super(context);
		typedArray = typedArray2;
		sp = context.getSharedPreferences("LoginPrefs", 0);
		resources = context.getResources();
		mDayLabelCalendar = Calendar.getInstance();
		mCalendar = Calendar.getInstance();
		today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		mDayOfWeekTypeface = resources.getString(R.string.sans_serif);
		mMonthTitleTypeface = resources.getString(R.string.sans_serif);

		// get from date details
		String stringDate=null;
		if ((sp.getString("fragmentName", "").toString()
				.equals("AdhocRequestTransport"))
				|| (sp.getString("fragmentName", "").toString()
						.equals("AdhocRequest"))) {
			 stringDate = sp.getString("custom_request_fromDate", "");
		} else {
			 stringDate = sp.getString("fromDate", "");
		}
		SimpleDateFormat f = new SimpleDateFormat("EEE, MMM d, yyyy");
		Date date = null;
		try {
			date = f.parse(stringDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 06

		if ((sp.getString("fragmentName", "").toString())
				.equals("AdhocRequestTransport")
				|| (sp.getString("fragmentName", "").toString())
						.equals("AdhocRequest")) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, 15);
			Date d = calendar.getTime();
			disableFromDay = (String) android.text.format.DateFormat.format(
					"dd", d); // 20
			disableMonth = (String) android.text.format.DateFormat.format("MM",
					d);
			disableYear = (String) android.text.format.DateFormat.format(
					"yyyy", d);

			prvDisableFromDay = (String) android.text.format.DateFormat.format(
					"dd", date); // 20
			prvDisableMonth = (String) android.text.format.DateFormat.format(
					"MM", date);
			prvDisableYear = (String) android.text.format.DateFormat.format(
					"yyyy", date); // 2013
		} else {
			disableFromDay = (String) android.text.format.DateFormat.format(
					"dd", date); // 20
			disableMonth = (String) android.text.format.DateFormat.format("MM",
					date);
			disableYear = (String) android.text.format.DateFormat.format(
					"yyyy", date); // 2013
		}
		/*
		 * mCurrentDayTextColor = typedArray.getColor(
		 * R.styleable.DayPickerView_colorCurrentDay,
		 * resources.getColor(R.color.normal_day));
		 */
		mMonthTextColor = typedArray.getColor(
				R.styleable.DayPickerView_colorMonthName,
				resources.getColor(R.color.normal_day));
		mDayTextColor = typedArray.getColor(
				R.styleable.DayPickerView_colorDayName,
				resources.getColor(R.color.normal_day));
		mDayWeekEndColor = typedArray.getColor(
				R.styleable.DayPickerView_colorDayName,
				resources.getColor(R.color.week_end));
		mDayNumColor = typedArray.getColor(
				R.styleable.DayPickerView_colorNormalDay,
				resources.getColor(R.color.normal_day));
		mPreviousDayColor = typedArray.getColor(
				R.styleable.DayPickerView_colorPreviousDay,
				resources.getColor(R.color.disable));
		mSelectedDaysColor = typedArray.getColor(
				R.styleable.DayPickerView_colorSelectedDayBackground,
				resources.getColor(R.color.selected_day_background));
		mMonthTitleBGColor = typedArray.getColor(
				R.styleable.DayPickerView_colorSelectedDayText,
				resources.getColor(R.color.selected_day_text));

		mDrawRect = typedArray.getBoolean(
				R.styleable.DayPickerView_drawRoundRect, true);

		mStringBuilder = new StringBuilder(50);

		MINI_DAY_NUMBER_TEXT_SIZE = typedArray.getDimensionPixelSize(
				R.styleable.DayPickerView_textSizeDay,
				resources.getDimensionPixelSize(R.dimen.text_size_day));
		MONTH_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(
				R.styleable.DayPickerView_textSizeMonth,
				resources.getDimensionPixelSize(R.dimen.text_size_month));
		MONTH_DAY_LABEL_TEXT_SIZE = typedArray.getDimensionPixelSize(
				R.styleable.DayPickerView_textSizeDayName,
				resources.getDimensionPixelSize(R.dimen.text_size_day_name));
		MONTH_HEADER_SIZE = typedArray.getDimensionPixelOffset(
				R.styleable.DayPickerView_headerMonthHeight,
				resources.getDimensionPixelOffset(R.dimen.header_month_height));
		DAY_SELECTED_CIRCLE_SIZE = typedArray.getDimensionPixelSize(
				R.styleable.DayPickerView_selectedDayRadius,
				resources.getDimensionPixelOffset(R.dimen.selected_day_radius));

		mRowHeight = ((typedArray.getDimensionPixelSize(
				R.styleable.DayPickerView_calendarHeight,
				resources.getDimensionPixelOffset(R.dimen.calendar_height)) - MONTH_HEADER_SIZE) / 6);

		isPrevDayEnabled = typedArray.getBoolean(
				R.styleable.DayPickerView_enablePreviousDay, false);

		initView();

	}

	private int calculateNumRows() {
		int offset = findDayOffset();
		int dividend = (offset + mNumCells) / mNumDays;
		int remainder = (offset + mNumCells) % mNumDays;
		return (dividend + (remainder > 0 ? 1 : 0));
	}

	private void drawMonthDayLabels(Canvas canvas) {
		int y = MONTH_HEADER_SIZE - (MONTH_DAY_LABEL_TEXT_SIZE / 2);
		int dayWidthHalf = (mWidth - mPadding * 2) / (mNumDays * 2);

		for (int i = 0; i < mNumDays; i++) {
			int calendarDay = (i + mWeekStart) % mNumDays;
			int x = (2 * i + 1) * dayWidthHalf + mPadding;
			mDayLabelCalendar.set(Calendar.DAY_OF_WEEK, calendarDay);
			if (mDateFormatSymbols.getShortWeekdays()[mDayLabelCalendar
					.get(Calendar.DAY_OF_WEEK)]
					.toUpperCase(Locale.getDefault()).equals("SAT")
					|| mDateFormatSymbols.getShortWeekdays()[mDayLabelCalendar
							.get(Calendar.DAY_OF_WEEK)].toUpperCase(
							Locale.getDefault()).equals("SUN")) {
				mMonthDayLabelPaint.setColor(mDayWeekEndColor);
			} else {
				mMonthDayLabelPaint.setColor(mDayTextColor);
			}
			canvas.drawText(
					mDateFormatSymbols.getShortWeekdays()[mDayLabelCalendar
							.get(Calendar.DAY_OF_WEEK)].toUpperCase(Locale
							.getDefault()), x, y + 20, mMonthDayLabelPaint);

		}
	}

	private void drawMonthTitle(Canvas canvas) {
		int x = (mWidth + 2 * mPadding) / 2;
		int y = (MONTH_HEADER_SIZE - MONTH_DAY_LABEL_TEXT_SIZE) / 2
				+ (MONTH_LABEL_TEXT_SIZE / 3);
		StringBuilder stringBuilder = new StringBuilder(getMonthAndYearString()
				.toLowerCase());
		stringBuilder.setCharAt(0,
				Character.toUpperCase(stringBuilder.charAt(0)));

		FontMetrics fm = new FontMetrics();
		mMonthTitlePaint.setColor(Color.LTGRAY);
		mMonthTitlePaint.setTextSize(35.0f);
		mMonthTitlePaint.getFontMetrics(fm);

		int margin = 5;
		mMonthTitlePaint.getFontMetrics(fm);

		Date d = new Date(Calendar.getInstance()
				.getTimeInMillis());

		// System.out.println("Month Name :"+new
		// SimpleDateFormat("MMMM").format(d));
		// int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
		// String currentMonth=new
		// SimpleDateFormat("MMMM").format(d)+" "+currentYear;
		// if (mMonth==currentMonth) {

		Context mContext = getContext();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		displayMetrics = mContext.getResources().getDisplayMetrics();
		int mScreenWidth = displayMetrics.widthPixels;
		Bitmap myBitmap = BitmapFactory.decodeResource(getResources(),
				R.color.primaryColor);
		Bitmap mBitmap = Bitmap.createScaledBitmap(myBitmap, mScreenWidth, 110,
				false);
		canvas.drawBitmap(mBitmap, 0, 0, mMonthTitlePaint);

		/*
		 * } else { canvas.drawRect( 0, 0, 500 +
		 * mMonthTitlePaint.measureText(stringBuilder .toString()) + margin, 100
		 * + fm.bottom + margin, mMonthTitlePaint); }
		 */
		int pos = mBitmap.getHeight() / 2;
		mMonthTitlePaint.setColor(Color.WHITE);
		// mMonthTitlePaint.setTextAlign(Align.CENTER);
		canvas.drawText(stringBuilder.toString(), x, pos + 10, mMonthTitlePaint);

		// canvas.drawText(stringBuilder.toString(), x, y, mMonthTitlePaint);
	}

	private int findDayOffset() {
		return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays)
				: mDayOfWeekStart) - mWeekStart;
	}

	private String getMonthAndYearString() {
		int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
				| DateUtils.FORMAT_NO_MONTH_DAY;
		mStringBuilder.setLength(0);
		long millis = mCalendar.getTimeInMillis();
		return DateUtils.formatDateRange(getContext(), millis, millis, flags);
	}

	private void onDayClick(SimpleMonthAdapter.CalendarDay calendarDay) {

		if ((sp.getString("fragmentName", "").toString())
				.equals("AdhocRequestTransport")
				|| (sp.getString("fragmentName", "").toString())
						.equals("AdhocRequest")) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, 16);
			if ((sp.getString("fromDateFlag", "").toString()).equals("true")) {

				if (mOnDayClickListener != null
						&& (isPrevDayEnabled || !((calendarDay.month == today.month)
								&& (calendarDay.year == today.year) && calendarDay.day < today.monthDay))
						&& calendarDay.getDate().before(c.getTime())) {
					mOnDayClickListener.onDayClick(this, calendarDay);
				}
			} else {
				if (mOnDayClickListener != null
						&& ((calendarDay.year > Integer
								.parseInt(prvDisableYear))
								|| (calendarDay.month + 1 > Integer
										.parseInt(prvDisableMonth)) || (calendarDay.day >= Integer
								.parseInt(prvDisableFromDay)))
						&& calendarDay.getDate().before(c.getTime())) {
					mOnDayClickListener.onDayClick(this, calendarDay);
				}
			}

		} else {
			if ((sp.getString("fromDateFlag", "").toString()).equals("true")) {
				if (mOnDayClickListener != null
						&& (isPrevDayEnabled || !((calendarDay.month == today.month)
								&& (calendarDay.year == today.year) && calendarDay.day < today.monthDay))) {
					mOnDayClickListener.onDayClick(this, calendarDay);
				}
			} else {

				if (mOnDayClickListener != null
						&& ((calendarDay.year > Integer.parseInt(disableYear))
								|| (calendarDay.month + 1 > Integer
										.parseInt(disableMonth)) || (calendarDay.day >= Integer
								.parseInt(disableFromDay)))) {
					mOnDayClickListener.onDayClick(this, calendarDay);
				}
			}
		}

	}

	private boolean sameDay(int monthDay, Time time) {
		return (mYear == time.year) && (mMonth == time.month)
				&& (monthDay + 1 == time.monthDay);
	}

	private boolean prevDay(int monthDay, Time time) {

		return ((mYear < time.year))
				|| (mYear == time.year && mMonth < time.month)
				|| (mMonth == time.month && monthDay < time.monthDay);
	}

	public boolean disableDay(int monthDay, int year, int month, int day) {

		return ((mYear < year)) || (mYear == year && mMonth + 1 < month)
				|| (mMonth + 1 == month && monthDay < day);
	}

	public boolean disableDayAfter(int monthDay, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(mYear, mMonth, monthDay);
		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.DATE, 15);
		return (c.getTime().after(c2.getTime()));
	}

	protected void drawMonthNums(Canvas canvas) {
		int y = (mRowHeight + MINI_DAY_NUMBER_TEXT_SIZE) / 2
				- DAY_SEPARATOR_WIDTH + MONTH_HEADER_SIZE;
		int paddingDay = (mWidth - 2 * mPadding) / (2 * mNumDays);
		int dayOffset = findDayOffset();
		int day = 1;

		// ////////
		while (day <= mNumCells) {
			/*
			 * Calendar c = Calendar.getInstance(); int dayOfWeek =
			 * c.get(Calendar.DAY_OF_WEEK);
			 * Log.d("sat *****************",String.valueOf(dayOfWeek)); int
			 * weekDay=getWeekdayOfMonth(mMonth,mYear); if(
			 * weekDay==Calendar.SUNDAY || day==Calendar.SATURDAY) {
			 * mDayTextColor = Color.RED;
			 * //Log.d("sat *****************",String.valueOf(day)+" sun"); }
			 */

			int x = paddingDay * (1 + dayOffset * 2) + mPadding;
			if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear)
					|| (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear)) {
				/*
				 * if (mDrawRect) { RectF rectF = new RectF(x -
				 * DAY_SELECTED_CIRCLE_SIZE, (y - MINI_DAY_NUMBER_TEXT_SIZE / 3)
				 * - DAY_SELECTED_CIRCLE_SIZE, x + DAY_SELECTED_CIRCLE_SIZE, (y
				 * - MINI_DAY_NUMBER_TEXT_SIZE / 3) + DAY_SELECTED_CIRCLE_SIZE);
				 *
				 * canvas.drawRoundRect(rectF, 10.0f, 10.0f,
				 * mSelectedCirclePaint);
				 */
				// canvas.drawCircle(50, 50 , 30, mSelectedCirclePaint);
				// } else
				canvas.drawCircle(x, y - MINI_DAY_NUMBER_TEXT_SIZE / 3,
						DAY_SELECTED_CIRCLE_SIZE, mSelectedCirclePaint);
				// }
			}
			if (mHasToday && (mToday == day)) {
				/*
				 * mMonthNumPaint.setColor(mCurrentDayTextColor);
				 * mMonthNumPaint.setTypeface(Typeface
				 * .defaultFromStyle(Typeface.BOLD));
				 */
			} else {

				SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date date = null;
				int m = mMonth + 1;
				try {
					date = inFormat.parse(day + "-" + m + "-" + mYear);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CharSequence dayName = android.text.format.DateFormat.format(
						"EEE", date);
				if ((((String) dayName).toUpperCase()).equals("SAT")
						|| (((String) dayName).toUpperCase()).equals("SUN")) {
					mMonthNumPaint.setColor(mDayWeekEndColor);
				} else {
					mMonthNumPaint.setColor(mDayTextColor);
				}
				mMonthNumPaint.setTypeface(Typeface
						.defaultFromStyle(Typeface.NORMAL));
			}

			if ((mMonth == mSelectedBeginMonth && mSelectedBeginDay == day && mSelectedBeginYear == mYear)
					|| (mMonth == mSelectedLastMonth && mSelectedLastDay == day && mSelectedLastYear == mYear))
				mMonthNumPaint.setColor(mMonthTitleBGColor);

			if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1
					&& mSelectedBeginYear == mSelectedLastYear
					&& mSelectedBeginMonth == mSelectedLastMonth
					&& mSelectedBeginDay == mSelectedLastDay
					&& day == mSelectedBeginDay
					&& mMonth == mSelectedBeginMonth && mYear == mSelectedBeginYear))

				mMonthNumPaint.setColor(mSelectedDaysColor);

			if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1
					&& mSelectedBeginYear == mSelectedLastYear && mSelectedBeginYear == mYear)
					&& (((mMonth == mSelectedBeginMonth && mSelectedLastMonth == mSelectedBeginMonth) && ((mSelectedBeginDay < mSelectedLastDay
							&& day > mSelectedBeginDay && day < mSelectedLastDay) || (mSelectedBeginDay > mSelectedLastDay
							&& day < mSelectedBeginDay && day > mSelectedLastDay)))
							|| ((mSelectedBeginMonth < mSelectedLastMonth
									&& mMonth == mSelectedBeginMonth && day > mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth
									&& mMonth == mSelectedLastMonth && day < mSelectedLastDay)) || ((mSelectedBeginMonth > mSelectedLastMonth
							&& mMonth == mSelectedBeginMonth && day < mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth
							&& mMonth == mSelectedLastMonth && day > mSelectedLastDay)))) {
				mMonthNumPaint.setColor(mSelectedDaysColor);
			}

			if ((mSelectedBeginDay != -1
					&& mSelectedLastDay != -1
					&& mSelectedBeginYear != mSelectedLastYear
					&& ((mSelectedBeginYear == mYear && mMonth == mSelectedBeginMonth) || (mSelectedLastYear == mYear && mMonth == mSelectedLastMonth)) && (((mSelectedBeginMonth < mSelectedLastMonth
					&& mMonth == mSelectedBeginMonth && day < mSelectedBeginDay) || (mSelectedBeginMonth < mSelectedLastMonth
					&& mMonth == mSelectedLastMonth && day > mSelectedLastDay)) || ((mSelectedBeginMonth > mSelectedLastMonth
					&& mMonth == mSelectedBeginMonth && day > mSelectedBeginDay) || (mSelectedBeginMonth > mSelectedLastMonth
					&& mMonth == mSelectedLastMonth && day < mSelectedLastDay))))) {
				mMonthNumPaint.setColor(mSelectedDaysColor);
			}

			if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1
					&& mSelectedBeginYear == mSelectedLastYear && mYear == mSelectedBeginYear)
					&& ((mMonth > mSelectedBeginMonth
							&& mMonth < mSelectedLastMonth && mSelectedBeginMonth < mSelectedLastMonth) || (mMonth < mSelectedBeginMonth
							&& mMonth > mSelectedLastMonth && mSelectedBeginMonth > mSelectedLastMonth))) {
				mMonthNumPaint.setColor(mSelectedDaysColor);
			}

			if ((mSelectedBeginDay != -1 && mSelectedLastDay != -1 && mSelectedBeginYear != mSelectedLastYear)
					&& ((mSelectedBeginYear < mSelectedLastYear && ((mMonth > mSelectedBeginMonth && mYear == mSelectedBeginYear) || (mMonth < mSelectedLastMonth && mYear == mSelectedLastYear))) || (mSelectedBeginYear > mSelectedLastYear && ((mMonth < mSelectedBeginMonth && mYear == mSelectedBeginYear) || (mMonth > mSelectedLastMonth && mYear == mSelectedLastYear))))) {
				mMonthNumPaint.setColor(mSelectedDaysColor);
			}
			/*
			 * //disable prev. day including today if (!isPrevDayEnabled &&
			 * prevDay(day, today) && today.month == mMonth && today.year ==
			 * mYear) {
			 */

			if (!isPrevDayEnabled && today.month == mMonth
					&& prevDay(day, today) && today.year == mYear) {
				mMonthNumPaint.setColor(mPreviousDayColor);

				/*
				 * mMonthNumPaint.setTypeface(Typeface
				 * .defaultFromStyle(Typeface.ITALIC));
				 */
			}
			// /////////////////////////////////////////////////////////////////////
			// disable date after 15 days of current date
			if ((sp.getString("fragmentName", "").toString())
					.equals("AdhocRequestTransport")
					|| (sp.getString("fragmentName", "").toString())
							.equals("AdhocRequest")) {

				if (disableDayAfter(day, Integer.parseInt(disableYear),
						Integer.parseInt(disableMonth),
						Integer.parseInt(disableFromDay))
						&& today.year == mYear) {
					mMonthNumPaint.setColor(mPreviousDayColor);
				}
				// disable day before from date
				if ((sp.getString("fromDateFlag", "").toString())
						.equals("false")) {
					if (disableDay(day, Integer.parseInt(prvDisableYear),
							Integer.parseInt(prvDisableMonth),
							Integer.parseInt(prvDisableFromDay))
							&& today.year == mYear) {

						mMonthNumPaint.setColor(mPreviousDayColor);
					}
				}
			} else {

				// disable before fromdates in custom leave req

				if ((sp.getString("fromDateFlag", "").toString())
						.equals("false")
						&& disableDay(day, Integer.parseInt(disableYear),
								Integer.parseInt(disableMonth),
								Integer.parseInt(disableFromDay))
						&& today.year == mYear) {

					mMonthNumPaint.setColor(mPreviousDayColor);

				}
			}

			// ////////////////////////////////////////////////////////////

			canvas.drawText(String.format("%d", day), x, y, mMonthNumPaint);

			dayOffset++;
			if (dayOffset == mNumDays) {
				dayOffset = 0;
				y += mRowHeight;
			}
			day++;
		}
	}

	public SimpleMonthAdapter.CalendarDay getDayFromLocation(float x, float y) {
		int padding = mPadding;
		if ((x < padding) || (x > mWidth - mPadding)) {
			return null;
		}

		int yDay = (int) (y - MONTH_HEADER_SIZE) / mRowHeight;
		int day = 1
				+ ((int) ((x - padding) * mNumDays / (mWidth - padding - mPadding)) - findDayOffset())
				+ yDay * mNumDays;

		if (mMonth > 11 || mMonth < 0
				|| CalendarUtils.getDaysInMonth(mMonth, mYear) < day || day < 1)
			return null;

		return new SimpleMonthAdapter.CalendarDay(mYear, mMonth, day);
	}

	protected void initView() {
		mMonthTitlePaint = new Paint();
		mMonthTitlePaint.setFakeBoldText(true);
		mMonthTitlePaint.setAntiAlias(true);
		mMonthTitlePaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
		mMonthTitlePaint.setTypeface(Typeface.create(mMonthTitleTypeface,
				Typeface.BOLD));
		mMonthTitlePaint.setColor(mMonthTextColor);

		mMonthTitlePaint.setTextAlign(Align.CENTER);
		mMonthTitlePaint.setStyle(Style.FILL);

		mMonthTitleBGPaint = new Paint();
		mMonthTitleBGPaint.setFakeBoldText(true);
		mMonthTitleBGPaint.setAntiAlias(true);
		mMonthTitleBGPaint.setColor(mMonthTitleBGColor);
		mMonthTitleBGPaint.setTextAlign(Align.CENTER);
		mMonthTitleBGPaint.setStyle(Style.FILL);

		mSelectedCirclePaint = new Paint();
		mSelectedCirclePaint.setFakeBoldText(true);
		mSelectedCirclePaint.setAntiAlias(true);
		mSelectedCirclePaint.setColor(mSelectedDaysColor);
		mSelectedCirclePaint.setTextAlign(Align.CENTER);
		mSelectedCirclePaint.setStyle(Style.FILL);
		mSelectedCirclePaint.setAlpha(SELECTED_CIRCLE_ALPHA);

		mMonthDayLabelPaint = new Paint();
		mMonthDayLabelPaint.setAntiAlias(true);
		mMonthDayLabelPaint.setTextSize(MONTH_DAY_LABEL_TEXT_SIZE);
		mMonthDayLabelPaint.setColor(mDayTextColor);

		mMonthDayLabelPaint.setTypeface(Typeface.create(mDayOfWeekTypeface,
				Typeface.NORMAL));
		mMonthDayLabelPaint.setStyle(Style.FILL);
		mMonthDayLabelPaint.setTextAlign(Align.CENTER);
		// mMonthDayLabelPaint.setFakeBoldText(true);

		mMonthNumPaint = new Paint();
		mMonthNumPaint.setAntiAlias(true);
		mMonthNumPaint.setTextSize(MINI_DAY_NUMBER_TEXT_SIZE);
		mMonthNumPaint.setStyle(Style.FILL);
		mMonthNumPaint.setTextAlign(Align.CENTER);
		mMonthNumPaint.setFakeBoldText(false);
	}

	protected void onDraw(Canvas canvas) {
		drawMonthTitle(canvas);
		drawMonthDayLabels(canvas);
		drawMonthNums(canvas);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight
				* mNumRows + MONTH_HEADER_SIZE + 40);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			SimpleMonthAdapter.CalendarDay calendarDay = getDayFromLocation(
					event.getX(), event.getY());
			if (calendarDay != null) {
				onDayClick(calendarDay);
			}
		}
		return true;
	}

	public void reuse() {
		mNumRows = DEFAULT_NUM_ROWS;
		requestLayout();
	}

	public void setMonthParams(HashMap<String, Integer> params) {
		if (!params.containsKey(VIEW_PARAMS_MONTH)
				&& !params.containsKey(VIEW_PARAMS_YEAR)) {
			throw new InvalidParameterException(
					"You must specify month and year for this view");
		}
		setTag(params);

		if (params.containsKey(VIEW_PARAMS_HEIGHT)) {
			mRowHeight = params.get(VIEW_PARAMS_HEIGHT);
			if (mRowHeight < MIN_HEIGHT) {
				mRowHeight = MIN_HEIGHT;
			}
		}
		if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_DAY)) {
			mSelectedBeginDay = params.get(VIEW_PARAMS_SELECTED_BEGIN_DAY);
		}
		if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_DAY)) {
			mSelectedLastDay = params.get(VIEW_PARAMS_SELECTED_LAST_DAY);
		}
		if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_MONTH)) {
			mSelectedBeginMonth = params.get(VIEW_PARAMS_SELECTED_BEGIN_MONTH);
		}
		if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_MONTH)) {
			mSelectedLastMonth = params.get(VIEW_PARAMS_SELECTED_LAST_MONTH);
		}
		if (params.containsKey(VIEW_PARAMS_SELECTED_BEGIN_YEAR)) {
			mSelectedBeginYear = params.get(VIEW_PARAMS_SELECTED_BEGIN_YEAR);
		}
		if (params.containsKey(VIEW_PARAMS_SELECTED_LAST_YEAR)) {
			mSelectedLastYear = params.get(VIEW_PARAMS_SELECTED_LAST_YEAR);
		}

		mMonth = params.get(VIEW_PARAMS_MONTH);
		mYear = params.get(VIEW_PARAMS_YEAR);

		mHasToday = false;
		mToday = -1;

		mCalendar.set(Calendar.MONTH, mMonth);
		mCalendar.set(Calendar.YEAR, mYear);
		mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);

		if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
			mWeekStart = 1;
			// mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
		} else {
			mWeekStart = mCalendar.getFirstDayOfWeek();

		}

		mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear);
		for (int i = 0; i < mNumCells; i++) {
			final int day = i + 1;
			if (sameDay(day, today)) {
				mHasToday = true;
				mToday = day;
			}

			mIsPrev = prevDay(day, today);

		}

		mNumRows = calculateNumRows();
	}

	public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
		mOnDayClickListener = onDayClickListener;
	}

	public static abstract interface OnDayClickListener {
		public abstract void onDayClick(SimpleMonthView simpleMonthView,
										SimpleMonthAdapter.CalendarDay calendarDay);
	}

}