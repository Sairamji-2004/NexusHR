import { useState } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";

export default function DashboardCalendar() {

    const [date, setDate] = useState(new Date());

    return (
        <div className="card">

            <h3>📅 Calendar</h3>

            <Calendar
                onChange={setDate}
                value={date}
            />

        </div>
    );
}