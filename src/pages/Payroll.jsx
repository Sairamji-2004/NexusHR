import { useEffect, useState } from "react";
import {
  PAYROLL_API,
  apiGet,
  downloadPayslip,
  exportPayrollExcel
} from "../api/api";
export default function Payroll() {
  const [payrolls, setPayrolls] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [month, setMonth] = useState("");
  const [year, setYear] = useState("");
  useEffect(() => {
    loadPayrolls();
  }, []);

  async function loadPayrolls() {
    try {
      const response = await apiGet(PAYROLL_API);

      console.log("Payroll Response:", response);

      setPayrolls(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error(err);
      alert("Failed to load payroll data");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <div className="container mt-4">
        <h2>Loading Payroll...</h2>
      </div>
    );
  }
const filteredPayrolls = payrolls.filter((payroll) => {
  const matchName =
    payroll.employeeName?.toLowerCase().includes(search.toLowerCase());

  const matchMonth =
    month === "" || payroll.month === month;

  const matchYear =
    year === "" || payroll.year.toString() === year;

  return matchName && matchMonth && matchYear;
});
  return (
    <div className="container mt-4">
    <div
  style={{
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "20px",
  }}
>
  <h1>Payroll</h1>

  <button
    className="primary-btn"
    onClick={exportPayrollExcel}
  >
    📊 Export Excel
  </button>
</div>
<div className="filter-bar">

  <input
    type="text"
    placeholder="Search Employee..."
    value={search}
    onChange={(e) => setSearch(e.target.value)}
  />

  <select
    value={month}
    onChange={(e) => setMonth(e.target.value)}
  >
    <option value="">All Months</option>
    <option>JANUARY</option>
    <option>FEBRUARY</option>
    <option>MARCH</option>
    <option>APRIL</option>
    <option>MAY</option>
    <option>JUNE</option>
    <option>JULY</option>
    <option>AUGUST</option>
    <option>SEPTEMBER</option>
    <option>OCTOBER</option>
    <option>NOVEMBER</option>
    <option>DECEMBER</option>
  </select>

  <input
    type="number"
    placeholder="Year"
    value={year}
    onChange={(e) => setYear(e.target.value)}
  />

</div>
      <table className="table table-bordered table-hover">
      <thead className="table-dark">
  <tr>
    <th>Employee Name</th>
    <th>Employee ID</th>
    <th>Month</th>
    <th>Year</th>
    <th>Basic</th>
    <th>HRA</th>
    <th>DA</th>
    <th>Allowance</th>
    <th>Deductions</th>
    <th>Net Salary</th>
    <th>Status</th>
    <th>Actions</th>
  </tr>
</thead>

        <tbody>
          {filteredPayrolls.length === 0 ? (
            <tr>
              <td colSpan="11" className="text-center">
                No Payroll Records
              </td>
            </tr>
          ) : (
            filteredPayrolls.map((payroll) => (
              <tr key={payroll.id}>
                <td>{payroll.employeeName}</td>
                <td>{payroll.employeeCode || payroll.employeeId}</td>
                <td>{payroll.month}</td>
                <td>{payroll.year}</td>
                <td>₹{payroll.basicSalary}</td>
                <td>₹{payroll.hra}</td>
                <td>₹{payroll.da}</td>
                <td>₹{payroll.specialAllowance}</td>
                <td>
                  ₹
                  {(payroll.pfDeduction || 0) +
                    (payroll.professionalTax || 0) +
                    (payroll.otherDeductions || 0)}
                </td>
                <td>
                  <strong>₹{payroll.netSalary}</strong>
                </td>
                <td>{payroll.status}</td>

<td>
  <button
    className="download-btn"
    onClick={() => downloadPayslip(payroll.id)}
  >
    📄 Download PDF
  </button>
</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}