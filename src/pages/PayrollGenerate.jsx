import { useEffect, useState } from "react";
import { EMPLOYEE_API, PAYROLL_API, apiGet, apiPost } from "../api/api";

export default function PayrollGenerate({ onSuccess }) {
  const [employees, setEmployees] = useState([]);

  const [form, setForm] = useState({
    employeeId: "",
    employeeName: "",
    month: "JULY",
    year: new Date().getFullYear(),
    basicSalary: "",
    hra: "",
    da: "",
    specialAllowance: "",
    otherDeductions: 0
  });

  useEffect(() => {
    loadEmployees();
  }, []);

  async function loadEmployees() {
    try {
      const response = await apiGet(EMPLOYEE_API);

      console.log("Employee API:", response);

      setEmployees(response.data.content || []);
    } catch (err) {
      console.error(err);
      setEmployees([]);
    }
  }

  function handleEmployee(e) {
    const emp = employees.find(
      employee => employee.id === e.target.value
    );

    if (!emp) return;

    const basic = Math.round((emp.currentCtc || 0) / 12);
    const hra = Math.round(basic * 0.20);
    const da = Math.round(basic * 0.10);
    const allowance = Math.round(basic * 0.05);

    setForm({
      employeeId: emp.id,
      employeeName: emp.fullName,
      month: "JULY",
      year: new Date().getFullYear(),
      basicSalary: basic,
      hra: hra,
      da: da,
      specialAllowance: allowance,
      otherDeductions: 0
    });
  }

  function handleChange(e) {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  }

  async function generatePayroll(e) {
  e.preventDefault();

  try {
    const result = await apiPost(
      `${PAYROLL_API}/generate`,
      {
        employeeId: form.employeeId,
        employeeName: form.employeeName,
        month: form.month,
        year: Number(form.year),
        basicSalary: Number(form.basicSalary),
        hra: Number(form.hra),
        da: Number(form.da),
        specialAllowance: Number(form.specialAllowance),
        otherDeductions: Number(form.otherDeductions),
      }
    );

    alert(result.message || "Payroll Generated Successfully!");

    // Optional: reset the form
    setForm({
      employeeId: "",
      employeeName: "",
      month: "JULY",
      year: new Date().getFullYear(),
      basicSalary: "",
      hra: "",
      da: "",
      specialAllowance: "",
      otherDeductions: 0,
    });

    if (onSuccess) {
      onSuccess();
    }
  } catch (err) {
    console.error(err);
    alert(err.message);
  }
}

  return (
    <div className="container mt-4">

      <div className="card shadow">

        <div className="card-header bg-success text-white">
          <h4 className="mb-0">Generate Payroll</h4>
        </div>

        <div className="card-body">

          <form onSubmit={generatePayroll}>

            <div className="row">

              <div className="col-md-6 mb-3">
                <label className="form-label">Employee</label>

                <select
                  className="form-select"
                  value={form.employeeId}
                  onChange={handleEmployee}
                  required
                >
                  <option value="">Select Employee</option>

                  {employees.map(emp => (
                    <option
                      key={emp.id}
                      value={emp.id}
                    >
                      {emp.employeeCode} - {emp.fullName}
                    </option>
                  ))}
                </select>
              </div>

              <div className="col-md-3 mb-3">
                <label className="form-label">Month</label>

                <input
                  className="form-control"
                  name="month"
                  value={form.month}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-3 mb-3">
                <label className="form-label">Year</label>

                <input
                  type="number"
                  className="form-control"
                  name="year"
                  value={form.year}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-3 mb-3">
                <label className="form-label">Basic Salary</label>

                <input
                  type="number"
                  className="form-control"
                  name="basicSalary"
                  value={form.basicSalary}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-3 mb-3">
                <label className="form-label">HRA</label>

                <input
                  type="number"
                  className="form-control"
                  name="hra"
                  value={form.hra}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-3 mb-3">
                <label className="form-label">DA</label>

                <input
                  type="number"
                  className="form-control"
                  name="da"
                  value={form.da}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-3 mb-3">
                <label className="form-label">Special Allowance</label>

                <input
                  type="number"
                  className="form-control"
                  name="specialAllowance"
                  value={form.specialAllowance}
                  onChange={handleChange}
                />
              </div>

              <div className="col-md-6 mb-3">
                <label className="form-label">Other Deductions</label>

                <input
                  type="number"
                  className="form-control"
                  name="otherDeductions"
                  value={form.otherDeductions}
                  onChange={handleChange}
                />
              </div>

            </div>

            <button
              type="submit"
              className="btn btn-success"
            >
              Generate Payroll
            </button>

          </form>

        </div>

      </div>

    </div>
  );
}   