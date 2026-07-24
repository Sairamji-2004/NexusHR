import { useState } from "react";
import { apiPost, LEAVE_API } from "../api/api";

export default function LeaveForm({ onSuccess }) {
  
    const [form, setForm] = useState({
        employeeId: "",
        employeeName: "",
        leaveType: "CASUAL",
        startDate: "",
        endDate: "",
        reason: ""
    });

    function handleChange(e) {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        });
    }

   async function handleSubmit(e) {
    e.preventDefault();

    const leaveData = {
        employeeId: form.employeeId,
        employeeName: form.employeeName,
        leaveType: form.leaveType,
        startDate: form.startDate,
        endDate: form.endDate,
        totalDays: calculateDays(form.startDate, form.endDate),
        reason: form.reason
    };

    console.log("SENDING TO BACKEND:", leaveData);

    try {
        const response = await apiPost(LEAVE_API, leaveData);

        console.log("POST RESPONSE:", response);

        alert("Leave Applied Successfully");

        if (onSuccess) {
            await onSuccess();
        }

        setForm({
            employeeId: "",
            employeeName: "",
            leaveType: "CASUAL",
            startDate: "",
            endDate: "",
            reason: ""
        });

    } catch (err) {
        console.error(err);
        alert("Failed to apply leave");
    }
} function calculateDays(start, end) {
    if (!start || !end) return 0;

    const startDate = new Date(start);
    const endDate = new Date(end);

    const diff =
        Math.floor(
            (endDate - startDate) / (1000 * 60 * 60 * 24)
        ) + 1;

    return diff > 0 ? diff : 0;
}
    return (
        <form onSubmit={handleSubmit} className="card p-3 mb-3">

            <input
                className="form-control mb-2"
                placeholder="Employee ID"
                name="employeeId"
                value={form.employeeId}
                onChange={handleChange}
            /> 
            <input
    type="text"
    name="employeeName"
    placeholder="Employee Name"
    value={form.employeeName}
    onChange={handleChange}
    className="form-control mb-2"
/>

            <select
                className="form-control mb-2"
                name="leaveType"
                value={form.leaveType}
                onChange={handleChange}
            >
                <option>CASUAL</option>
                <option>SICK</option>
                <option>EARNED</option>
            </select>

            <input
                type="date"
                className="form-control mb-2"
                name="startDate"
                value={form.startDate}
                onChange={handleChange}
            />

            <input
                type="date"
                className="form-control mb-2"
                name="endDate"
                value={form.endDate}
                onChange={handleChange}
            />

            <textarea
                className="form-control mb-2"
                placeholder="Reason"
                name="reason"
                value={form.reason}
                onChange={handleChange}
            />

            <button className="btn btn-success">
                Submit Leave
            </button>

        </form>
    );
}