import { useState } from "react";
import { apiPost, EMPLOYEE_API } from "../api/api";

export default function EmployeeForm({ onSuccess }) {
  const [employee, setEmployee] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    hireDate: "",
    dateOfBirth: "",
    jobTitle: "",
    currentSalary: "",
    departmentId: "",
    managerId: "",
    employmentType: "FULL_TIME",
  });

  const [loading, setLoading] = useState(false);

  function handleChange(e) {
    setEmployee({
      ...employee,
      [e.target.name]: e.target.value,
    });
  }

  async function handleSubmit(e) {
    e.preventDefault();

    try {
      setLoading(true);

      const request = {
        ...employee,
        currentSalary: Number(employee.currentSalary),
      };

      console.log("Sending Employee:", request);

      await apiPost(EMPLOYEE_API, request);

      alert("Employee Created Successfully");

      setEmployee({
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        hireDate: "",
        dateOfBirth: "",
        jobTitle: "",
        currentSalary: "",
        departmentId: "",
        managerId: "",
        employmentType: "FULL_TIME",
      });

      onSuccess();
    } catch (err) {
      alert(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="employee-form">
      <h3>Add Employee</h3>

      <form onSubmit={handleSubmit}>
        <select
          name="departmentId"
          value={employee.departmentId}
          onChange={handleChange}
          required
        >
          <option value="">Select Department</option>

          <option value="aaaaaaaa-0000-0000-0000-000000000001">
            Human Resources
          </option>

          <option value="aaaaaaaa-0000-0000-0000-000000000002">
            Engineering
          </option>

          <option value="aaaaaaaa-0000-0000-0000-000000000003">
            Finance
          </option>
        </select>

        <input
          name="managerId"
          placeholder="Manager ID (Optional)"
          value={employee.managerId}
          onChange={handleChange}
        />

        <input
          name="firstName"
          placeholder="First Name"
          value={employee.firstName}
          onChange={handleChange}
          required
        />

        <input
          name="lastName"
          placeholder="Last Name"
          value={employee.lastName}
          onChange={handleChange}
          required
        />

        <input
          type="email"
          name="email"
          placeholder="Email"
          value={employee.email}
          onChange={handleChange}
          required
        />

        <input
          name="phone"
          placeholder="Phone"
          value={employee.phone}
          onChange={handleChange}
          required
        />

        <input
          name="jobTitle"
          placeholder="Job Title"
          value={employee.jobTitle}
          onChange={handleChange}
          required
        />

        <input
          type="number"
          name="currentSalary"
          placeholder="Current Salary"
          value={employee.currentSalary}
          onChange={handleChange}
          required
        />

        <input
          type="date"
          name="hireDate"
          value={employee.hireDate}
          onChange={handleChange}
          required
        />

        <select
          name="employmentType"
          value={employee.employmentType}
          onChange={handleChange}
        >
          <option value="FULL_TIME">FULL TIME</option>
          <option value="PART_TIME">PART TIME</option>
          <option value="CONTRACT">CONTRACT</option>
        </select>

        <button type="submit" disabled={loading}>
          {loading ? "Creating..." : "Create Employee"}
        </button>
      </form>
    </div>
  );
}