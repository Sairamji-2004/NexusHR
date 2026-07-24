import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import DashboardChart from "../components/DashboardChart";
import { Link } from "react-router-dom";
import DashboardCalendar from "../components/DashboardCalendar";
import {
  apiGet,
  EMPLOYEE_API,
  ATTENDANCE_API,
  PAYROLL_API,
  LEAVE_API,
  PERFORMANCE_API,
  NOTIFICATION_API,
} from "../api/api";


export default function Dashboard() {

 const {
    fullName,
    role,
    employeeId
} = useAuth();
const [recentActivities, setRecentActivities] = useState([]);

  const [stats, setStats] = useState({

    employees: 0,
    attendance: 0,
    leave: 0,
    payroll: 0,
    performance: 0,
    notifications: 0,

    activeEmployees: 0,

  });



useEffect(() => {

    loadDashboard();

    const interval = setInterval(() => {
        loadDashboard();
    }, 5000);

    return () => clearInterval(interval);

}, []);


  async function loadDashboard() {

    try {


      const [
        employees,
        attendance,
        leave,
        payroll,
        performance,
        notifications

      ] = await Promise.all([

        apiGet(`${EMPLOYEE_API}?page=0&size=100`),

        apiGet(ATTENDANCE_API),

        apiGet(LEAVE_API),

        apiGet(PAYROLL_API),

        apiGet(PERFORMANCE_API),

       role === "ROLE_ADMIN"
    ? apiGet(NOTIFICATION_API)
    : apiGet(`${NOTIFICATION_API}/employee/${employeeId}`),

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



      const notificationList =
        Array.isArray(notifications)
          ? notifications
          : notifications.data || [];

const latestActivities = [...notificationList]
  .sort(
    (a, b) =>
      new Date(b.createdAt) -
      new Date(a.createdAt)
  )
  .slice(0, 5);
const employeeAttendance =
    attendanceList.filter(
        a => a.employeeId === employeeId
    );

const employeeLeaves =
    leaveList.filter(
        l => l.employeeId === employeeId
    );

const employeePayroll =
    payrollList.filter(
        p => p.employeeId === employeeId
    );

const employeePerformance =
    performanceList.filter(
        p => p.employeeId === employeeId
    );

      setStats({

    employees:
        role === "ROLE_ADMIN"
            ? employeeList.length
            : 1,

    attendance:
        role === "ROLE_ADMIN"
            ? attendanceList.length
            : employeeAttendance.length,

    leave:
        role === "ROLE_ADMIN"
            ? leaveList.length
            : employeeLeaves.length,

    payroll:
        role === "ROLE_ADMIN"
            ? payrollList.length
            : employeePayroll.length,

    performance:
        role === "ROLE_ADMIN"
            ? performanceList.length
            : employeePerformance.length,

    notifications:
        notificationList.length,

    activeEmployees:
        role === "ROLE_ADMIN"
            ? employeeList.filter(
                emp => emp.status === "ACTIVE"
              ).length
            : 1

});
setRecentActivities(latestActivities);


    } catch (err) {

      console.error(
        "Dashboard loading failed:",
        err
      );

    }

  }



  return (

    <div className="page">

      <h1>
        Welcome, {fullName}
      </h1>


      <h4>
        NexusHR Dashboard
      </h4>



   {role === "ROLE_ADMIN" ? (

    <>
        <div className="card">
            <h3>👥 Total Employees</h3>
            <h2>{stats.employees}</h2>
            <p>Registered Employees</p>
        </div>

        <div className="card">
            <h3>📅 Attendance Records</h3>
            <h2>{stats.attendance}</h2>
            <p>Total Attendance Entries</p>
        </div>

        <div className="card">
            <h3>📝 Leave Requests</h3>
            <h2>{stats.leave}</h2>
            <p>Total Leave Applications</p>
        </div>

        <div className="card">
            <h3>💰 Payroll Records</h3>
            <h2>{stats.payroll}</h2>
            <p>Generated Payrolls</p>
        </div>

        <div className="card">
            <h3>⭐ Performance Reviews</h3>
            <h2>{stats.performance}</h2>
            <p>Total Reviews</p>
        </div>

        <div className="card">
            <h3>🔔 Notifications</h3>
            <h2>{stats.notifications}</h2>
            <p>System Notifications</p>
        </div>

        <div className="card">
            <h3>✅ Active Employees</h3>
            <h2>{stats.activeEmployees}</h2>
            <p>Currently Active</p>
        </div>
    </>

) : (

    <>
        <div className="card">
            <h3>👤 My Profile</h3>
            <h2>1</h2>
            <p>Your Employee Account</p>
        </div>

        <div className="card">
            <h3>📅 My Attendance</h3>
            <h2>{stats.attendance}</h2>
            <p>Your Attendance Records</p>
        </div>

        <div className="card">
            <h3>📝 My Leave</h3>
            <h2>{stats.leave}</h2>
            <p>Your Leave Requests</p>
        </div>

        <div className="card">
            <h3>💰 My Payroll</h3>
            <h2>{stats.payroll}</h2>
            <p>Your Payroll Records</p>
        </div>

        <div className="card">
            <h3>⭐ My Reviews</h3>
            <h2>{stats.performance}</h2>
            <p>Your Performance Reviews</p>
        </div>

        <div className="card">
            <h3>🔔 My Notifications</h3>
            <h2>{stats.notifications}</h2>
            <p>Your Notifications</p>
        </div>
    </>

)}
<div className="dashboard-section">

    <DashboardCalendar />

</div>

<div className="dashboard-section">

    <h2>Quick Actions</h2>

    <div className="dashboard-cards">

        {role === "ROLE_ADMIN" ? (

            <>
                <Link to="/employees" className="card">
                    <h3>👥 Manage Employees</h3>
                    <p>Add, Edit and View Employees</p>
                </Link>

                <Link to="/payroll/generate" className="card">
                    <h3>💰 Generate Payroll</h3>
                    <p>Create Monthly Payroll</p>
                </Link>

                <Link to="/performance" className="card">
                    <h3>⭐ Performance Reviews</h3>
                    <p>Manage Employee Reviews</p>
                </Link>

                <Link to="/notifications" className="card">
                    <h3>🔔 Notifications</h3>
                    <p>View System Notifications</p>
                </Link>
            </>

        ) : (

            <>
                <Link to="/attendance" className="card">
                    <h3>📅 My Attendance</h3>
                    <p>Check In / Check Out</p>
                </Link>

                <Link to="/leave" className="card">
                    <h3>📝 Apply Leave</h3>
                    <p>Submit Leave Request</p>
                </Link>

                <Link to="/payroll" className="card">
                    <h3>💰 My Payroll</h3>
                    <p>Download Payslip</p>
                </Link>

                <Link to="/notifications" className="card">
                    <h3>🔔 Notifications</h3>
                    <p>View Latest Notifications</p>
                </Link>
            </>

        )}

    </div>

</div>




      <div className="dashboard-section">

        <h2>
          Recent Activity
        </h2>


      <tbody>

  {recentActivities.length === 0 ? (

    <tr>
      <td colSpan="2" className="text-center">
        No recent activity.
      </td>
    </tr>

  ) : (

    recentActivities.map((activity) => (

      <tr key={activity.id}>

        <td>
          {activity.title}
        </td>

        <td>
          {activity.message}
          <br />
          <small className="text-muted">
            {new Date(activity.createdAt).toLocaleString()}
          </small>
        </td>

      </tr>

    ))

  )}

</tbody>

      </div>


    </div>

  );

}