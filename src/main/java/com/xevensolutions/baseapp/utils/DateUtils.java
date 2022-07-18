package com.xevensolutions.baseapp.utils;

import static com.xevensolutions.baseapp.utils.TextUtils.isStringEmpty;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Trace;
import android.text.format.Time;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class DateUtils {

    public static final String DATE_TIME_24H_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_MDY_FORMAT = "MM/dd/yyyy";


    public static final String DATE_FORMAT_DASH = "yyyy-MM-dd";
    public static final String TIME_24H_FORMAT = "HH:mm:ss";
    public static final String TIME_FORMAT_AM_PM = "hh:mm aa";


    /**
     * This function accepts time in format "hh:mm:ss" and returns the date by appending the current
     * date with the time e.g. accepts "05:00:00" and returns "2020-02-12T05:00:00"
     *
     * @param time
     * @return
     */
    public static String getFullDateFromTime(String time) {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH)
                + "T" + time;
    }

    public interface OnDateSelect {
        void onDateSelected(String formattedDate, Calendar calendar);
    }

    public interface TimeSelectionListener {
        void onTimeSelected(String time, Calendar calendar);
    }

    public static void pickServerDate(Context context, OnDateSelect onDateSelect) {
        Calendar cal = Calendar.getInstance();
        android.app.DatePickerDialog.OnDateSetListener fromListener = (view1, year, monthOfYear, dayOfMonth) -> {

            Calendar calFrom = Calendar.getInstance();
            calFrom.set(year, monthOfYear, dayOfMonth);
            String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(calFrom.getTime());
//            dob = calFrom.getTime();
//            dateString = DateTimeUtils.getFormattedDate(calFrom);
            onDateSelect.onDateSelected(dateString, calFrom);
        };

        new android.app.DatePickerDialog(context, fromListener, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    @SuppressLint("DefaultLocale")
    public static void pickTime(Context context, boolean is24HourView, TimeSelectionListener listener) {
        Calendar calendar = Calendar.getInstance();

        // Time Selection Listener
        TimePickerDialog.OnTimeSetListener timeSelectionListener = (view, hourOfDay, minute) -> {
            int seconds = 0;
            String selectedTime = String.format("%02d:%02d:%02d", hourOfDay, minute, seconds);
            listener.onTimeSelected(selectedTime, calendar);
        };

        new TimePickerDialog(context, timeSelectionListener, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), is24HourView).show();
    }

    public static String getFormattedTimeToShow(String dateTime) {
        try {
            Trace.beginSection("get formatted date");
            String formattedDate = DateUtils.getFormattedTime(DateUtils.getLocalDateStringFromUTC(dateTime).split("T")[1]);
            Trace.endSection();
            return formattedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static int getDaysRemaining(String expiry) {

        int days = 0;
        try {
            days = (int) DateUtils.getDateDiff(Calendar.getInstance().getTime(), DateUtils.getDateFromString(expiry)
                    , TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (days < 0)
            days = 0;

        return days;

    }

    public static int getDaysListed(String expiry) {

        int days = 0;
        try {
            days = (int) DateUtils.getDateDiff(DateUtils.getDateFromString(expiry), Calendar.getInstance().getTime()
                    , TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (days < 0)
            days = 0;

        return days;

    }


    public static String concatDateAndTime(String date, String time) {
        return date
                + "T" + time;
    }


    /**
     * This funcation takes in date in the format "yyyy-MM-dd'T'HH:mm:ss" and returns the time part in the format
     * "hh:mm aa"
     *
     * @param date
     * @return
     */
    public static String getOnlyTimeFromDate(String date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_24H_FORMAT, Locale.US);
        try {
            Date formattedDate = simpleDateFormat.parse(date);
            SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT_AM_PM, Locale.US);
            return timeFormat.format(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getHoursFromTime(String taskTime) {
        try {
            if (taskTime == null)
                return 0;
            String[] taskTimes = taskTime.split(":");
            if (taskTimes.length > 0) {
                return Integer.parseInt(taskTimes[0]);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getMinsFromTime(String taskTime) {
        try {
            if (taskTime == null)
                return 0;
            String[] taskTimes = taskTime.split(":");
            if (taskTimes.length > 1) {
                return Integer.parseInt(taskTimes[1]);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public static String getLocalTimeFromUTCTime(String time, String dateTime) {

        if (dateTime == null)
            dateTime = getFormattedDateTime(Calendar.getInstance());
        try {
            return DateUtils.getOnlyTimeFromDate(DateUtils.getLocalDateStringFromUTC(DateUtils.concatDateAndTime(
                    DateUtils.getOnlyDateFromDateTime(dateTime), time
            )));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * This function takes in time in format 18:00:00 and returns the time in format 06:00 pm
     *
     * @param time
     * @return
     */
    public static String getFormattedTime(String time) {


        if (time == null)
            return "";
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        try {
            Date date = dateFormat.parse(time);
            DateFormat formattedTime = new SimpleDateFormat("hh:mm aa", Locale.US);
            return formattedTime.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getFormattedDate(int dayOfMonth, int month, int year) {
        return year + "-" + (month > 9 ? month : "0" + month) + "-" + (dayOfMonth > 9 ? dayOfMonth : "0" + dayOfMonth) + "T00:00:00";
    }


    public static String getPassedTime(long timeStamp) {
        return (String) android.text.format.DateUtils.getRelativeTimeSpanString(timeStamp, System.currentTimeMillis(),
                android.text.format.DateUtils.SECOND_IN_MILLIS);
    }


    public static String getDatePartFromDateTime(String date) throws Exception {
        String datePart = null;
        if (date.contains("T")) {
            datePart = date.split("T")[0];
        } else {
            datePart = date;
        }

        return datePart;

    }


    public static String getFormattedDateTime(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_24H_FORMAT, Locale.US);
        return simpleDateFormat.format(calendar.getTime());
    }


    public static Date getDateFromString(String date) {
        if (date == null)
            return null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_24H_FORMAT, Locale.US);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static Date convertStringToDate(String date, String dateFormat) {
        Date date1 = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            date1 = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date1;
    }

    public static Date getDateFromMonthDayAndYear(int month, int day, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        Date date = calendar.getTime();

        String monthh = month < 10 ? "0" + month : String.valueOf(month);
        String dayy = day < 10 ? "0" + day : String.valueOf(day);

        return DateUtils.convertStringToDate(year + "-" + monthh + "-" + dayy + "T00:00:00", DateUtils.DATE_TIME_24H_FORMAT);
    }

    public static String getFormattedDuration(int hours, int mins) {
        String hourString = hours < 10 ? "0" + hours : String.valueOf(hours);
        String minString = mins < 10 ? "0" + mins : String.valueOf(mins);

        return hourString + ":" + minString;

    }


    public static ArrayList<Date> sortDatesArray(ArrayList<Date> unsortedDates) {
        Date[] unsortedDatesArray = new Date[unsortedDates.size()];
        for (int i = 0; i < unsortedDates.size(); i++) {
            unsortedDatesArray[i] = unsortedDates.get(i);
        }
        Arrays.sort(unsortedDatesArray);

        return new ArrayList<>(Arrays.asList(unsortedDatesArray));

    }


    public static String getTimeAgo(String currentDateTime, boolean isUTC) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_24H_FORMAT, Locale.US);
            if (isUTC)
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date past = format.parse(currentDateTime);
            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
//
//          System.out.println(TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + " milliseconds ago");
//          System.out.println(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minutes ago");
//          System.out.println(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hours ago");
//          System.out.println(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago");


            if (seconds < 60) {
                return ("Just Now");
            } else if (minutes < 60) {
                return (minutes + " minute(s) ago");
            } else if (hours < 24) {
                return (hours + " hour(s) ago");
            } else if (days < 7) {
                return (days + " day(s) ago");
            } else
                return null;


        } catch (Exception j) {
            j.printStackTrace();
        }
        return null;
    }


    public static String getTimeInAmPmFormat(String time) {


        SimpleDateFormat twentyFourHourFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        try {
            return timeFormat.format(twentyFourHourFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }


    public static String getTime(String dateString) {
        Date date = getDateFromString(dateString);
        return getTime(date);
    }

    public static String getTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);
        int secs = calendar.get(Calendar.SECOND);
        return hour + ":" + mins + ":" + secs;
    }


    public static Date getCurrentDateWithoutTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }


    public static int getAge(String dateOfBirth) {
        String dates[] = dateOfBirth.split("/");
        if (dates.length > 2) {
            int month = Integer.parseInt(dates[0]);
            int day = Integer.parseInt(dates[1]);
            int year = Integer.parseInt(dates[2]);

            LocalDate birthdate = new LocalDate(year, month, day);
            LocalDate now = new LocalDate();                        //Today's date

            Period period = new Period(birthdate, now, PeriodType.yearMonthDay());
            return period.getYears();
        }
        return 0;
    }

    public static Date getCurrentDateWithNoTime() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }


    public static String getDateStringInUTC(String dateTime) {


        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        DateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            String utcFormatDateString = utcFormat.format(localFormat.parse(dateTime));
            Date utcDateInLocalFormat = localFormat.parse(utcFormatDateString);
            return localFormat.format(utcDateInLocalFormat);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }

        return null;

    }


    public static String getFormattedDateToShow(Calendar calandar) {

        int year = calandar.get(Calendar.YEAR);
        int month = calandar.get(Calendar.MONTH);
        int day = calandar.get(Calendar.DAY_OF_MONTH);
        return getFormattedDateToShow(year, month + 1, day);
    }

    public static String getFormattedDateToShow(String date) {
        if (date == null)
            return "";

        String formattedDate;
        String[] dateParts = date.split("T");
        String onlyDatee = dateParts.length > 0 ? dateParts[0] : "";
        String[] onlyDateParts = onlyDatee.split("-");

        if (onlyDateParts.length >= 3) {
            try {
                return getFormattedDateToShow(Integer.parseInt(onlyDateParts[0]), Integer.parseInt(onlyDateParts[1])
                        , Integer.parseInt(onlyDateParts[2]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return date;
    }


    public static String getFormattedDateToShow(int year, int month, int day) {
        return month + "/" + day + "/" + year;
    }

    public static String getFormattedDate(String date) {
        if (date == null)
            return "";

        if (date.contains("/"))      // date contains forwards slashes and is already formatted
            return date;

        String dateOnly = getOnlyDateFromDateTime(date);

        String[] dateSplitted = dateOnly.split("-");
        String year = dateSplitted.length > 0 ? dateSplitted[0] : "";
        String month = dateSplitted.length > 1 ? dateSplitted[1] : "";
        String day = dateSplitted.length > 2 ? dateSplitted[2] : "";

        return day + "/" + month + "/" + year;
        /*    String monthName = "";
        try {
            monthName = getMonthNameFromMonthNumber(Integer.parseInt(month));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthName + " " + day + ", " + year;*/
    }

    /**
     * this method takes in a dateTime String like 2012-3-4T12:36:45 and returns only the date part
     * by removing the time and returns a string like 2012-3-4
     *
     * @param dateTimeString
     * @return
     */
    public static String getOnlyDateFromDateTime(String dateTimeString) {
        String[] splittedDateTime = dateTimeString.split("T");
        return splittedDateTime.length > 0 ? splittedDateTime[0] : "";
    }


    public static String getLocalDateStringFromUTC(String UTCDateTime) {
        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date;
        try {
            date = utcFormat.parse(UTCDateTime);
            DateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
            pstFormat.setTimeZone(TimeZone.getDefault());
            assert date != null;
            return pstFormat.format(date);
        } catch (AssertionError | Exception e) {
            e.printStackTrace();
        }

        return UTCDateTime;
    }

    public static String getLocalDateStringFromUTC(long UTCtimeStamp) {

        Date date = new Date(UTCtimeStamp);


        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date UTCdate = null;
        try {
            UTCdate = utcFormat.parse(utcFormat.format(date));
            DateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
            pstFormat.setTimeZone(TimeZone.getTimeZone("PST"));
            return pstFormat.format(UTCdate);
        } catch (ParseException e) {
            e.printStackTrace();
            DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            return isoFormat.format(date);


        }


    }

    public static Date getLocalDateFromUTC(long UTCtimeStamp) {

        Date testDate
                = new Date(UTCtimeStamp);

        long localTimeStamp = getLocalTimeStampFromUTCTimeStamp(UTCtimeStamp);

        Date date = new Date(localTimeStamp);

        return date;

/*

        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date UTCdate = null;
        try {
            UTCdate = utcFormat.parse(utcFormat.format(date));
            DateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            pstFormat.setTimeZone(TimeZone.getDefault());
            return pstFormat.parse(pstFormat.format(UTCdate));
        } catch (ParseException e) {
            e.printStackTrace();
            DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                return isoFormat.parse(isoFormat.format(date));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }


        }

        return null;
*/

    }

    public static long getLocalTimeStampFromUTCTimeStamp(long UTCtimeStamp) {


        Time time = new Time();
        time.set(UTCtimeStamp + TimeZone.getDefault().getOffset(UTCtimeStamp));
        return time.toMillis(true);


    }


    public static long getLocalTimeStampFromUTCDateString(String utcString) {

        try {
            Date date = new SimpleDateFormat(DATE_TIME_24H_FORMAT, Locale.US).parse(utcString);
            Time time = new Time();
            time.set(date.getTime() + TimeZone.getDefault().getOffset(date.getTime()));
            return time.toMillis(true);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1;

    }


    public static Date getDate(int year, int monthOfYear, int dayOfMonth) {


        Calendar calendar = getCalendarInstance(year, monthOfYear, dayOfMonth);
        return calendar.getTime();
    }

    public static Calendar getCalendarInstance(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar;
    }

    public static boolean isCurrentTimeBeforeFivePm() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        return currentHour < 17;
    }

    /**
     * Get a diff between two dates
     *
     * @param date1    the oldest date
     * @param date2    the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static boolean isToday(String creationDate) {
        if (creationDate == null)
            return false;

        return getDateDiff(getDateFromString(creationDate), new Date(), TimeUnit.HOURS) < 24;
    }

    public static String getFormattedDateTimeToShow(String dateTime) {
        String localDate = getLocalDateStringFromUTC(dateTime);
        String[] dateParts = localDate.split("T");

        String formattedDateTime = getFormattedDateToShow(localDate);
        if (dateParts.length > 0) {
            try {
                String time = getFormattedTime(dateParts[1]);
                if (!isStringEmpty(time))
                    formattedDateTime = formattedDateTime + " at " + time;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return formattedDateTime;
    }

    /**
     * @return DateTime with newly specified format
     */
    public static String changeFormat(String dateTime, String fromFormat, String toFormat) {
        SimpleDateFormat sfdInput = new SimpleDateFormat(fromFormat, Locale.ROOT);
        SimpleDateFormat sdfOutput = new SimpleDateFormat(toFormat, Locale.ROOT);

        try {
            if (dateTime.contains("+"))
                dateTime = dateTime.substring(0, dateTime.lastIndexOf("+"));

            Date date = sfdInput.parse(dateTime);
            assert date != null;
            return sdfOutput.format(date);
        } catch (AssertionError | Exception e) {
            e.printStackTrace();
        }

        return dateTime; // Return Same DateTime, only if unable to change format
    }
}
