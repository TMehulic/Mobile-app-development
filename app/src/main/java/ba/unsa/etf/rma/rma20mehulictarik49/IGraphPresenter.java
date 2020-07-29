package ba.unsa.etf.rma.rma20mehulictarik49;

import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public interface IGraphPresenter {

    List<BarEntry> getWeekTotalEntries();
    List<BarEntry> getWeekEarnEntries();
    List<BarEntry> getWeekConsumEntries();
    List<BarEntry> getDayTotalEntries();
    List<BarEntry> getDayEarnEntries();
    List<BarEntry> getDayConsumEntries();
    List<BarEntry> getMonthTotalEntries();
    List<BarEntry> getMonthEarnEntries();
    List<BarEntry> getMonthConsumEntries();


}
