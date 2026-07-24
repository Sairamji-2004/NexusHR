import { useState } from "react";
import { LEAVE_API, apiPost } from "../api/api";

export default function ApplyLeave() {
console.log("APPLY LEAVE PAGE LOADED");
  const [form, setForm] = useState({
    employeeId: "",
    employeeName: "",
    leaveType: "CASUAL",
    startDate: "",
    endDate: "",
    reason: ""
  });

  const [message, setMessage] = useState("");

  function handleChange(e) {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  }


  function calculateDays(start, end) {

    if (!start || !end) return 0;

    const startDate = new Date(start);
    const endDate = new Date(end);

    const difference =
      Math.ceil(
        (endDate - startDate) / (1000 * 60 * 60 * 24)
      ) + 1;

    return difference;
  }


 async function submitLeave(e) {
    e.preventDefault();

    const leaveData = {
        ...form,
        totalDays: calculateDays(form.startDate, form.endDate)
    };

    console.log("Sending:", leaveData);

    try {
        const response = await apiPost(LEAVE_API, leaveData);

        console.log("POST SUCCESS:", response);

        setMessage("Leave applied successfully");

        setTimeout(() => {
            window.location.href = "/leave";
        }, 500);

    } catch (error) {
        console.error("POST ERROR:", error);
        setMessage("Failed to apply leave");
    }
}


  return (
    <div>

      <h2>Apply Leave</h2>

      <form onSubmit={submitLeave}>


        <input
          name="employeeId"
          placeholder="Employee ID"
          value={form.employeeId}
          onChange={handleChange}
          required
        />


        <input
          name="employeeName"
          placeholder="Employee Name"
          value={form.employeeName}
          onChange={handleChange}
          required
        />


        <select
          name="leaveType"
          value={form.leaveType}
          onChange={handleChange}
        >
          <option value="CASUAL">
            Casual Leave
          </option>

          <option value="SICK">
            Sick Leave
          </option>

          <option value="PAID">
            Paid Leave
          </option>

        </select>


        <label>
          Start Date
        </label>

        <input
          type="date"
          name="startDate"
          value={form.startDate}
          onChange={handleChange}
          required
        />


        <label>
          End Date
        </label>

        <input
          type="date"
          name="endDate"
          value={form.endDate}
          onChange={handleChange}
          required
        />


        <textarea
          name="reason"
          placeholder="Reason"
          value={form.reason}
          onChange={handleChange}
        />


        <button type="submit">
          Apply Leave
        </button>


      </form>


      <p>
        {message}
      </p>

    </div>
  );
}