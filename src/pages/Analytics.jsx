import { useEffect, useState } from "react";
import AnalyticsCharts from "../components/AnalyticsCharts";
import {
    apiGet,
    EMPLOYEE_API,
    ATTENDANCE_API,
    LEAVE_API,
    PAYROLL_API,
    PERFORMANCE_API
} from "../api/api";

export default function Analytics() {

    const [analytics, setAnalytics] = useState({

        employees: 0,
        activeEmployees: 0,
        attendance: 0,
        leave: 0,
        payroll: 0,
        performance: 0

    });

    useEffect(() => {

        loadAnalytics();

    }, []);

    async function loadAnalytics() {

        try {

            const [

                employees,
                attendance,
                leave,
                payroll,
                performance

            ] = await Promise.all([

                apiGet(`${EMPLOYEE_API}?page=0&size=100`),

                apiGet(ATTENDANCE_API),

                apiGet(LEAVE_API),

                apiGet(PAYROLL_API),

                apiGet(PERFORMANCE_API)

            ]);

            const employeeList =
                Array.isArray(employees)
                    ? employees
                    : employees.data?.content || [];

            const attendanceList =
                Array.isArray(attendance)
                    ? attendance
                    : attendance.data || [];

            const leaveList =
                Array.isArray(leave)
                    ? leave
                    : leave.data || [];

            const payrollList =
                Array.isArray(payroll)
                    ? payroll
                    : payroll.data || [];

            const performanceList =
                Array.isArray(performance)
                    ? performance
                    : performance.data || [];

            setAnalytics({

                employees: employeeList.length,

                activeEmployees:
                    employeeList.filter(
                        e => e.status === "ACTIVE"
                    ).length,

                attendance: attendanceList.length,

                leave: leaveList.length,

                payroll: payrollList.length,

                performance: performanceList.length

            });

        } catch (err) {

            console.log(err);

        }

    }

    return (

        <div className="page">

            <h1>Analytics</h1>

            <div className="dashboard-cards">

                <div className="card">
                    <h3>Total Employees</h3>
                    <h1>{analytics.employees}</h1>
                </div>

                <div className="card">
                    <h3>Active Employees</h3>
                    <h1>{analytics.activeEmployees}</h1>
                </div>

                <div className="card">
                    <h3>Attendance</h3>
                    <h1>{analytics.attendance}</h1>
                </div>

                <div className="card">
                    <h3>Leave Requests</h3>
                    <h1>{analytics.leave}</h1>
                </div>

                <div className="card">
                    <h3>Payroll</h3>
                    <h1>{analytics.payroll}</h1>
                </div>

                <div className="card">
                    <h3>Performance Reviews</h3>
                    <h1>{analytics.performance}</h1>
                </div>

            </div>

            <div className="dashboard-section">

                <h2>Analytics Charts</h2>

                <AnalyticsCharts analytics={analytics} />

            </div>

        </div>

    );

}