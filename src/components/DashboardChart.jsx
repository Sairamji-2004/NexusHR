import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    ArcElement,
    Title,
    Tooltip,
    Legend,
} from "chart.js";

import {
    Bar,
    Doughnut,
    Pie
} from "react-chartjs-2";

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement,
    ArcElement,
    Title,
    Tooltip,
    Legend
);

export default function DashboardChart({ stats }) {

    const barData = {

        labels: [
            "Employees",
            "Attendance",
            "Leave",
            "Payroll",
            "Performance",
            "Notifications"
        ],

        datasets: [
            {
                label: "Total Records",

                data: [
                    stats.employees || 0,
                    stats.attendance || 0,
                    stats.leave || 0,
                    stats.payroll || 0,
                    stats.performance || 0,
                    stats.notifications || 0
                ]
            }
        ]
    };


    const doughnutData = {

        labels: [
            "Employees",
            "Attendance",
            "Leave",
            "Payroll",
            "Performance",
            "Notifications"
        ],

        datasets: [
            {
                data: [
                    stats.employees || 0,
                    stats.attendance || 0,
                    stats.leave || 0,
                    stats.payroll || 0,
                    stats.performance || 0,
                    stats.notifications || 0
                ]
            }
        ]
    };


    const pieData = {

        labels: [
            "Active Employees",
            "Other Employees"
        ],

        datasets: [
            {
                data: [
                    stats.activeEmployees || 0,
                    (stats.employees || 0) - (stats.activeEmployees || 0)
                ]
            }
        ]
    };


    const options = {

        responsive: true,

        plugins: {

            legend: {
                position: "top"
            }

        }

    };


    return (

        <>

            <div className="mb-5">

                <h4 className="text-center mb-3">
                    System Overview
                </h4>

                <Bar
                    data={barData}
                    options={options}
                />

            </div>


            <div className="row">

                <div className="col-md-6">

                    <h4 className="text-center mb-3">
                        Module Distribution
                    </h4>

                    <Doughnut
                        data={doughnutData}
                        options={options}
                    />

                </div>


                <div className="col-md-6">

                    <h4 className="text-center mb-3">
                        Employee Status
                    </h4>

                    <Pie
                        data={pieData}
                        options={options}
                    />

                </div>

            </div>

        </>

    );

}