import { useEffect, useState } from "react";
import {
  EMPLOYEE_API,
  apiGet,
  apiDelete,
  apiPatch,
  exportEmployeesExcel,
} from "../api/api";

import EmployeeForm from "./EmployeeForm";

export default function Employees() {
  const [employees, setEmployees] = useState([]);
  const [filteredEmployees, setFilteredEmployees] = useState([]);
  const [selectedEmployee, setSelectedEmployee] = useState(null);

  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadEmployees();
  }, []);

  async function loadEmployees() {
    setLoading(true);

    try {
      const response = await apiGet(`${EMPLOYEE_API}?page=0&size=20`);

      const list = response.data?.content || [];

      setEmployees(list);
      setFilteredEmployees(list);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    const keyword = search.toLowerCase();

    setFilteredEmployees(
      employees.filter(
        (emp) =>
          emp.employeeCode?.toLowerCase().includes(keyword) ||
          emp.fullName?.toLowerCase().includes(keyword) ||
          emp.email?.toLowerCase().includes(keyword) ||
          emp.designation?.toLowerCase().includes(keyword)
      )
    );
  }, [search, employees]);

  async function deleteEmployee(id) {
    if (!window.confirm("Are you sure you want to delete this employee?")) {
      return;
    }

    try {
      await apiDelete(`${EMPLOYEE_API}/${id}`);

      alert("Employee deleted successfully");

      loadEmployees();
    } catch (err) {
      alert(err.message);
    }
  }

  async function updateStatus(id, status) {
    try {
      await apiPatch(`${EMPLOYEE_API}/${id}/status?status=${status}`);

      alert("Status updated");

      loadEmployees();
    } catch (err) {
      alert(err.message);
    }
  }

  async function viewEmployee(id) {
    try {
      const response = await apiGet(`${EMPLOYEE_API}/${id}`);

      setSelectedEmployee(response.data);
    } catch (err) {
      alert(err.message);
    }
  }

  return (
    <div className="page">
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "20px",
        }}
      >
        <h1>Employees</h1>

        <button
          className="export-btn"
          onClick={exportEmployeesExcel}
        >
          📥 Export Excel
        </button>
      </div>

      <EmployeeForm onSuccess={loadEmployees} />

      <input
        type="text"
        placeholder="Search by Employee Code, Name, Email or Designation..."
        className="search-box"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      {loading && <p>Loading...</p>}

      {error && <div className="error-banner">{error}</div>}

      {!loading && !error && (
        <>
          <table className="data-table">
            <thead>
              <tr>
                <th>Employee Code</th>
                <th>Name</th>
                <th>Designation</th>
                <th>Email</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {filteredEmployees.map((emp) => (
                <tr key={emp.id}>
                  <td>{emp.employeeCode}</td>
                  <td>{emp.fullName}</td>
                  <td>{emp.designation}</td>
                  <td>{emp.email}</td>

                  <td>
                    <select
                      value={emp.status}
                      onChange={(e) =>
                        updateStatus(emp.id, e.target.value)
                      }
                    >
                      <option value="ACTIVE">ACTIVE</option>
                      <option value="SUSPENDED">SUSPENDED</option>
                      <option value="NOTICE_PERIOD">
                        NOTICE_PERIOD
                      </option>
                      <option value="TERMINATED">
                        TERMINATED
                      </option>
                    </select>
                  </td>

                  <td>
                    <button
                      className="view-btn"
                      onClick={() => viewEmployee(emp.id)}
                    >
                      View
                    </button>

                    {" "}

                    <button
                      className="delete-btn"
                      onClick={() => deleteEmployee(emp.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}

              {filteredEmployees.length === 0 && (
                <tr>
                  <td colSpan="6" style={{ textAlign: "center" }}>
                    No employees found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>

          {selectedEmployee && (
            <div
              className="employee-details"
              style={{
                marginTop: "30px",
                padding: "20px",
                border: "1px solid #ddd",
                borderRadius: "8px",
                background: "#fff",
              }}
            >
              <h2>Employee Details</h2>

              <p><strong>Employee Code:</strong> {selectedEmployee.employeeCode}</p>
              <p><strong>Name:</strong> {selectedEmployee.fullName}</p>
              <p><strong>Email:</strong> {selectedEmployee.email}</p>
              <p><strong>Phone:</strong> {selectedEmployee.phone}</p>
              <p><strong>Designation:</strong> {selectedEmployee.designation}</p>
              <p><strong>Department:</strong> {selectedEmployee.departmentName}</p>
              <p><strong>Status:</strong> {selectedEmployee.status}</p>
              <p><strong>Employment Type:</strong> {selectedEmployee.employmentType}</p>
              <p><strong>Hire Date:</strong> {selectedEmployee.hireDate}</p>
              <p><strong>Current Salary:</strong> ₹{selectedEmployee.currentCtc}</p>
            </div>
          )}
        </>
      )}
    </div>
  );
}