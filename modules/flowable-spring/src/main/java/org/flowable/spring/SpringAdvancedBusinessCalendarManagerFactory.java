package org.flowable.spring;

import org.flowable.engine.common.impl.util.DefaultClockImpl;
import org.flowable.engine.common.runtime.Clock;
import org.flowable.engine.impl.calendar.AdvancedCycleBusinessCalendar;
import org.flowable.engine.impl.calendar.BusinessCalendarManager;
import org.flowable.engine.impl.calendar.DueDateBusinessCalendar;
import org.flowable.engine.impl.calendar.DurationBusinessCalendar;
import org.flowable.engine.impl.calendar.MapBusinessCalendarManager;

/**
 * Creates an advanced cycle business calendar manager (ACBCM). The ACBCM can handle daylight savings changes when the scheduled time zone is different than the server time zone.
 * <p>
 * Create a factory bean
 * 
 * <pre>
 * &lt;bean id="businessCalendarManagerFactory" class="org.flowable.spring.SpringAdvancedBusinessCalendarManagerFactory" /&gt;
 * </pre>
 * 
 * Add the manager to your org.flowable.spring.SpringProcessEngineConfiguration bean
 * 
 * <pre>
 *  &lt;bean id="processEngineConfiguration" class="org.flowable.spring.SpringProcessEngineConfiguration"&gt;
 *    ...
 *    &lt;property name="businessCalendarManager"&gt;
 *      &lt;bean id="advancedBusinessCalendarManager" factory-bean="businessCalendarManagerFactory" factory-method="getBusinessCalendarManager" /&gt;
 *    &lt;/property&gt;
 *    ...
 *  &lt;/bean&gt;
 * </pre>
 * 
 * @author mseiden
 * @see AdvancedCycleBusinessCalendar
 */
public class SpringAdvancedBusinessCalendarManagerFactory {

    private Integer defaultScheduleVersion;

    private Clock clock;

    public Integer getDefaultScheduleVersion() {
        return defaultScheduleVersion;
    }

    public void setDefaultScheduleVersion(Integer defaultScheduleVersion) {
        this.defaultScheduleVersion = defaultScheduleVersion;
    }

    public Clock getClock() {
        if (clock == null) {
            clock = new DefaultClockImpl();
        }
        return clock;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public BusinessCalendarManager getBusinessCalendarManager() {
        MapBusinessCalendarManager mapBusinessCalendarManager = new MapBusinessCalendarManager();
        mapBusinessCalendarManager.addBusinessCalendar(DurationBusinessCalendar.NAME, new DurationBusinessCalendar(getClock()));
        mapBusinessCalendarManager.addBusinessCalendar(DueDateBusinessCalendar.NAME, new DueDateBusinessCalendar(getClock()));
        mapBusinessCalendarManager.addBusinessCalendar(AdvancedCycleBusinessCalendar.NAME, new AdvancedCycleBusinessCalendar(getClock(), defaultScheduleVersion));

        return mapBusinessCalendarManager;
    }

}
