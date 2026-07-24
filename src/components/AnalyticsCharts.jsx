import {
    Chart as ChartJS,
    ArcElement,
    CategoryScale,
    LinearScale,
    BarElement,
    PointElement,
    LineElement,
    Tooltip,
    Legend,
} from "chart.js";

import { Pie, Bar, Line } from "react-chartjs-2";

ChartJS.register(
    ArcElement,
    CategoryScale,
    LinearScale,
    BarElement,
    PointElement,
    LineElement,
    Tooltip,
    Legend
);

export default function AnalyticsCharts({ analytics }) {

    const chartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                position: "top",
            },
        },
    };

    const pieData = {
        labels: ["Active", "Inactive"],
        datasets: [
            {
                data: [
                    analytics.activeEmployees,
                    analytics.employees - analytics.activeEmployees,
                ],
                backgroundColor: [
                    "#22c55e",
                    "#ef4444",
                ],
            },
        ],
    };

    const barData = {
        labels: [
            "Attendance",
            "Leave",
            "Payroll",
            "Performance",
        ],
        datasets: [
            {
                label: "Records",
                data: [
                    analytics.attendance,
                    analytics.leave,
                    analytics.payroll,
                    analytics.performance,
                ],
                backgroundColor: "#2563eb",
            },
        ],
    };

    const lineData = {
        labels: [
            "Employees",
            "Attendance",
            "Leave",
            "Payroll",
            "Performance",
        ],
        datasets: [
            {
                label: "Overview",
                data: [
                    analytics.employees,
                    analytics.attendance,
                    analytics.leave,
                    analytics.payroll,
                    analytics.performance,
                ],
                borderColor: "#16a34a",
                backgroundColor: "#16a34a",
                tension: 0.3,
            },
        ],
    };

    return (

        <div className="dashboard-cards">

 <div className="card analytics-chart-card">
    <h3>👥 Employee Status</h3>

    <div className="analytics-chart">
        <Pie
            data={pieData}
            options={{
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: "bottom",
                    },
                },
            }}
        />
    </div>

</div>
 <div className="card analytics-chart-card">
  <h3>📊 Module Records</h3>

    <div className="analytics-chart">
        <Bar
            data={barData}
            options={{
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false,
                    },
                },
                scales: {
                    y: {
                        beginAtZero: true,
                    },
                },
            }}
        />
    </div>

</div>

          <div className="card analytics-chart-card">
    <h3>📈 Overall Trend</h3>
<div className="analytics-chart">
        <Line
            data={lineData}
            options={{
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false,
                    },
                },
                scales: {
                    y: {
                        beginAtZero: true,
                    },
                },
            }}
        />
    </div>

</div>

        </div>

    );

}