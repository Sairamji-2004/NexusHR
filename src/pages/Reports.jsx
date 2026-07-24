const reports = [

    {
        title: "Employee Report",
        icon: "👥",
        description: "Download all employee records."
    },

    {
        title: "Attendance Report",
        icon: "📅",
        description: "Download attendance history."
    },

    {
        title: "Leave Report",
        icon: "📝",
        description: "Download leave applications."
    },

    {
        title: "Payroll Report",
        icon: "💰",
        description: "Download payroll reports."
    },

    {
        title: "Performance Report",
        icon: "⭐",
        description: "Download performance reviews."
    }

];

export default function Reports() {

    return (

        <div className="page">

            <h1>Reports</h1>

            <p>
                Generate and export HR reports.
            </p>

            <div className="dashboard-cards">

                {reports.map((report) => (

                    <div
                        className="card"
                        key={report.title}
                    >

                        <h2>{report.icon}</h2>

                        <h3>{report.title}</h3>

                        <p>{report.description}</p>

                        <div
                            style={{
                                marginTop: "20px",
                                display: "flex",
                                gap: "10px"
                            }}
                        >

                            <button className="export-btn">
                                Excel
                            </button>

                            <button className="download-btn">
                                PDF
                            </button>

                        </div>

                    </div>

                ))}

            </div>

        </div>

    );

}