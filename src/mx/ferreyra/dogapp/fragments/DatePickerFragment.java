package mx.ferreyra.dogapp.fragments;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment  implements DatePickerDialog.OnDateSetListener{

	public int year = -1;
	public int month = -1;
	public int day = -1;
	private MyDate date;


	public void setDate (int year, int month, int day){

		if (year != -1 && month != -1 && day != -1 ){
			this.year = year;
			this.month = month;
			this.day = day;
		}
	}

	public void setInterface (MyDate Date){
		this.date = Date;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int tmp_year = c.get(Calendar.YEAR);
		int tmp_month = c.get(Calendar.MONTH);
		int tmp_day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it

		if (year!= -1 && month != -1 && day != -1 ){
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		else{
			return new DatePickerDialog(getActivity(), this, tmp_year, tmp_month, tmp_day);
		}
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
		this.year = year;
		this.month = month;
		this.day = day;

		if (date != null)
			date.getDate (year, month, day);

	}

	public interface MyDate{
		void getDate (int year, int month, int day);
	}

}
