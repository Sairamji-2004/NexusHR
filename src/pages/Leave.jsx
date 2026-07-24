import { useEffect, useState } from "react";
import {
    apiGet,
    LEAVE_API,
    approveLeave,
    rejectLeave,
    deleteLeave
} from "../api/api";
import LeaveForm from "../components/LeaveForm";

export default function Leave() {

    const [leaves, setLeaves] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [loading, setLoading] = useState(false);

    const [search, setSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [leaveTypeFilter, setLeaveTypeFilter] = useState("ALL");
const [selectedLeave, setSelectedLeave] = useState(null);
    useEffect(() => {
        loadLeaves();
    }, []);

    async function loadLeaves() {

        try {

            setLoading(true);

            const response = await apiGet(LEAVE_API);

            setLeaves(response);

        } catch (error) {

            console.error(error);

        } finally {

            setLoading(false);

        }

    }

    async function handleSuccess() {

        await loadLeaves();

        setShowForm(false);

    }

    async function handleApprove(id) {

        try {

            await approveLeave(id);

            alert("Leave Approved");

            await loadLeaves();

        } catch (error) {

            console.error(error);

            alert("Approve Failed");

        }

    }

    async function handleReject(id) {

        try {

            await rejectLeave(id);

            alert("Leave Rejected");

            await loadLeaves();

        } catch (error) {

            console.error(error);

            alert("Reject Failed");

        }

    }

    async function handleDelete(id) {

        const ok = window.confirm(
            "Are you sure you want to delete this leave?"
        );

        if (!ok) return;

        try {

            await deleteLeave(id);

            alert("Leave Deleted Successfully");

            await loadLeaves();

        } catch (error) {

            console.error(error);

            alert("Delete Failed");

        }

    }

    const filteredLeaves = leaves.filter((leave) => {

        const matchesSearch =
            leave.employeeName
                ?.toLowerCase()
                .includes(search.toLowerCase());

        const matchesStatus =
            statusFilter === "ALL" ||
            leave.status === statusFilter;

        const matchesType =
            leaveTypeFilter === "ALL" ||
            leave.leaveType === leaveTypeFilter;

        return (
            matchesSearch &&
            matchesStatus &&
            matchesType
        );

    });

    return (

        <div className="container mt-4">

            <h2 className="mb-3">
                Leave Management
            </h2>

            <button
                className="btn btn-primary mb-4"
                onClick={() => setShowForm(!showForm)}
            >
                {showForm ? "Close Form" : "Apply Leave"}
            </button>

            {showForm && (
                <LeaveForm onSuccess={handleSuccess} />
            )}

            {/* Dashboard */}

            <div className="row mb-4">

                <div className="col-md-3">
                    <div className="card bg-primary text-white">
                        <div className="card-body text-center">
                            <h5>Total</h5>
                            <h2>{leaves.length}</h2>
                        </div>
                    </div>
                </div>

                <div className="col-md-3">
                    <div className="card bg-warning text-dark">
                        <div className="card-body text-center">
                            <h5>Pending</h5>
                            <h2>
                                {leaves.filter(
                                    l => l.status === "PENDING"
                                ).length}
                            </h2>
                        </div>
                    </div>
                </div>

                <div className="col-md-3">
                    <div className="card bg-success text-white">
                        <div className="card-body text-center">
                            <h5>Approved</h5>
                            <h2>
                                {leaves.filter(
                                    l => l.status === "APPROVED"
                                ).length}
                            </h2>
                        </div>
                    </div>
                </div>

                <div className="col-md-3">
                    <div className="card bg-danger text-white">
                        <div className="card-body text-center">
                            <h5>Rejected</h5>
                            <h2>
                                {leaves.filter(
                                    l => l.status === "REJECTED"
                                ).length}
                            </h2>
                        </div>
                    </div>
                </div>

            </div>

            {/* Filters */}

            <div className="row mb-4">

                <div className="col-md-4">

                    <input
                        className="form-control"
                        placeholder="Search Employee..."
                        value={search}
                        onChange={(e) =>
                            setSearch(e.target.value)
                        }
                    />

                </div>

                <div className="col-md-4">

                    <select
                        className="form-select"
                        value={statusFilter}
                        onChange={(e) =>
                            setStatusFilter(e.target.value)
                        }
                    >

                        <option value="ALL">All Status</option>
                        <option value="PENDING">Pending</option>
                        <option value="APPROVED">Approved</option>
                        <option value="REJECTED">Rejected</option>

                    </select>

                </div>

                <div className="col-md-4">

                    <select
                        className="form-select"
                        value={leaveTypeFilter}
                        onChange={(e) =>
                            setLeaveTypeFilter(e.target.value)
                        }
                    >

                        <option value="ALL">All Leave Types</option>
                        <option value="CASUAL">Casual</option>
                        <option value="SICK">Sick</option>
                        <option value="EARNED">Earned</option>

                    </select>

                </div>

            </div>

            {loading ? (

                <h4 className="text-center">
                    Loading...
                </h4>

            ) : (

                <table className="table table-bordered table-striped table-hover">

                    <thead className="table-dark">

                        <tr>

                            <th>Employee</th>
                            <th>Leave Type</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Total Days</th>
                            <th>Reason</th>
                            <th>Status</th>
                            <th>Action</th>

                        </tr>

                    </thead>

                    <tbody>

                        {filteredLeaves.length === 0 ? (

                            <tr>

                                <td
                                    colSpan="8"
                                    className="text-center"
                                >
                                    No Leave Records Found
                                </td>

                            </tr>

                        ) : (

                            filteredLeaves.map((leave) => (

                                <tr key={leave.id}>

                                    <td>{leave.employeeName}</td>

                                    <td>{leave.leaveType}</td>

                                    <td>{leave.startDate}</td>

                                    <td>{leave.endDate}</td>

                                    <td>{leave.totalDays}</td>

                                    <td>{leave.reason}</td>

                                    <td>

                                        <span
                                            className={
                                                leave.status === "APPROVED"
                                                    ? "badge bg-success"
                                                    : leave.status === "REJECTED"
                                                    ? "badge bg-danger"
                                                    : "badge bg-warning text-dark"
                                            }
                                        >
                                            {leave.status}
                                        </span>

                                    </td>

                                    <td>

                                        {leave.status === "PENDING" && (

                                            <>
                                                <button
                                                    className="btn btn-success btn-sm me-2"
                                                    onClick={() =>
                                                        handleApprove(leave.id)
                                                    }
                                                >
                                                    Approve
                                                </button>

                                                <button
                                                    className="btn btn-danger btn-sm me-2"
                                                    onClick={() =>
                                                        handleReject(leave.id)
                                                    }
                                                >
                                                    Reject
                                                </button>
                                            </>

                                        )}

                                        <button
                                            className="btn btn-outline-danger btn-sm"
                                            onClick={() =>
                                                handleDelete(leave.id)
                                            }
                                        >
                                            Delete
                                        </button>
                                        <button
    className="btn btn-info btn-sm ms-2"
    onClick={() => setSelectedLeave(leave)}
>
    View
</button>

                                    </td>

                                </tr>

                            ))

                        )}

                    </tbody>

                </table>

            )}
            {selectedLeave && (
                <>
                    <div
                        className="modal fade show"
                        style={{ display: "block", backgroundColor: "rgba(0,0,0,0.5)" }}
                    >
                        <div className="modal-dialog">
                            <div className="modal-content">

                                <div className="modal-header">
                                    <h5 className="modal-title">
                                        Leave Details
                                    </h5>

                                    <button
                                        className="btn-close"
                                        onClick={() => setSelectedLeave(null)}
                                    ></button>
                                </div>

                                <div className="modal-body">

                                    <p>
                                        <strong>Employee:</strong>{" "}
                                        {selectedLeave.employeeName}
                                    </p>

                                    <p>
                                        <strong>Employee ID:</strong>{" "}
                                        {selectedLeave.employeeId}
                                    </p>

                                    <p>
                                        <strong>Leave Type:</strong>{" "}
                                        {selectedLeave.leaveType}
                                    </p>

                                    <p>
                                        <strong>Start Date:</strong>{" "}
                                        {selectedLeave.startDate}
                                    </p>

                                    <p>
                                        <strong>End Date:</strong>{" "}
                                        {selectedLeave.endDate}
                                    </p>

                                    <p>
                                        <strong>Total Days:</strong>{" "}
                                        {selectedLeave.totalDays}
                                    </p>

                                    <p>
                                        <strong>Reason:</strong>{" "}
                                        {selectedLeave.reason}
                                    </p>

                                    <p>
                                        <strong>Status:</strong>{" "}
                                        {selectedLeave.status}
                                    </p>

                                </div>

                                <div className="modal-footer">

                                    <button
                                        className="btn btn-secondary"
                                        onClick={() => setSelectedLeave(null)}
                                    >
                                        Close
                                    </button>

                                </div>

                            </div>
                        </div>
                    </div>

                    <div className="modal-backdrop fade show"></div>
                </>
            )}

        </div>

    );

}