import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import {
    apiGet,
    apiPost,
    apiPut,
    apiDelete,
    PERFORMANCE_API,
    EMPLOYEE_API
} from "../api/api";
export default function Performance() {

   const emptyForm = {
    employeeId: "",
    employeeName: "",
    department: "",
    rating: 5,
    feedback: "",
    reviewer: "",
    reviewDate: ""
};
const { roles, employeeId } = useAuth();

const isAdmin =
    roles.includes("ROLE_HR_ADMIN") ||
    roles.includes("ROLE_SUPER_ADMIN");
const [reviews, setReviews] = useState([]);
const [form, setForm] = useState(emptyForm);
const [editingId, setEditingId] = useState(null);
const [loading, setLoading] = useState(true);
const [search, setSearch] = useState("");
const [employees, setEmployees] = useState([]);


   async function loadPerformance() {

    try {

        let data;

        if (isAdmin) {

            data = await apiGet(PERFORMANCE_API);

        } else {

            data = await apiGet(
                `${PERFORMANCE_API}/employee/${employeeId}`
            );

        }

        setReviews(Array.isArray(data) ? data : []);

    } catch (err) {

        console.error(err);
        alert("Failed to load performance reviews");

    } finally {

        setLoading(false);

    }

}

    useEffect(() => {

        loadPerformance();
        loadEmployees();
    }, []);

    function handleChange(e) {

        setForm({
            ...form,
            [e.target.name]: e.target.value
        });

    }
    function handleChange(e) {
    setForm({
        ...form,
        [e.target.name]: e.target.value
    });
}

async function handleSubmit(e) {
    e.preventDefault();

    try {

        if (editingId) {

            await apiPut(
                `${PERFORMANCE_API}/${editingId}`,
                form
            );

            alert("Performance review updated successfully");

        } else {

            await apiPost(
                PERFORMANCE_API,
                form
            );

            alert("Performance review added successfully");
        }

        setForm(emptyForm);
        setEditingId(null);

        loadPerformance();

    } catch (err) {

        console.error(err);
        alert(err.message);
    }
}

function editReview(review) {

    setEditingId(review.id);

    setForm({
        employeeId: review.employeeId,
        employeeName: review.employeeName,
        department: review.department,
        rating: review.rating,
        feedback: review.feedback,
        reviewer: review.reviewer,
        reviewDate: review.reviewDate
    });

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}
async function loadEmployees() {

    try {

        const data = await apiGet(EMPLOYEE_API);

        setEmployees(Array.isArray(data) ? data : []);

    } catch (err) {

        console.error(err);

    }

}

async function deleteReview(id) {

    if (!window.confirm("Delete this review?")) {
        return;
    }

    try {

        await apiDelete(`${PERFORMANCE_API}/${id}`);

        alert("Deleted successfully");

        loadPerformance();

    } catch (err) {

        console.error(err);
        alert(err.message);
    }
}

function cancelEdit() {

    setEditingId(null);

    setForm(emptyForm);
}

    async function handleSubmit(e) {

        e.preventDefault();

        try {

            if (editingId) {

                await apiPut(
                    `${PERFORMANCE_API}/${editingId}`,
                    form
                );

                alert("Performance updated successfully");

            } else {

                await apiPost(
                    PERFORMANCE_API,
                    form
                );

                alert("Performance added successfully");
            }

            setForm(emptyForm);
            setEditingId(null);

            loadPerformance();

        } catch (err) {

            console.error(err);
            alert(err.message);

        }

    }

    function handleEdit(review) {

        setEditingId(review.id);

        setForm({
            employeeId: review.employeeId,
            employeeName: review.employeeName,
            department: review.department,
            rating: review.rating,
            feedback: review.feedback,
            reviewer: review.reviewer,
            reviewDate: review.reviewDate
        });

    }

    async function handleDelete(id) {

        if (!window.confirm("Delete this performance review?")) {
            return;
        }

        try {

            await apiDelete(`${PERFORMANCE_API}/${id}`);

            alert("Deleted successfully");

            loadPerformance();

        } catch (err) {

            console.error(err);
            alert(err.message);

        }

    }

const filteredReviews = reviews.filter(r =>
    r.employeeName
        ?.toLowerCase()
        .includes(search.toLowerCase())
);
return (

<div className="container py-4">

    {isAdmin && (
    <div className="card shadow mb-4">

        <div className="card-header bg-primary text-white">

            <h3 className="mb-0">
                {editingId
                    ? "Edit Performance Review"
                    : "Add Performance Review"}
            </h3>

        </div>

        <div className="card-body">

            <form onSubmit={handleSubmit}>

                <div className="row">

                    <div className="col-md-6 mb-3">

                        <label className="form-label">
                            Employee ID
                        </label>

    <input
    className="form-control"
    name="employeeId"
    value={form.employeeId}
    onChange={handleChange}
/>
                    </div>

                    <div className="col-md-6 mb-3">

                        <label className="form-label">
                            Employee Name
                        </label>

            <input
    className="form-control"
    name="employeeName"
    value={form.employeeName}
    onChange={handleChange}
    required
/>

                    </div>

                    <div className="col-md-6 mb-3">

                        <label className="form-label">
                            Department
                        </label>

     <input
    className="form-control"
    name="department"
    value={form.department}
    onChange={handleChange}
    required
/>

                    </div>

                    <div className="col-md-6 mb-3">

                        <label className="form-label">
                            Rating
                        </label>

                        <select
                            className="form-select"
                            name="rating"
                            value={form.rating}
                            onChange={handleChange}
                        >
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                        </select>

                    </div>

                    <div className="col-md-12 mb-3">

                        <label className="form-label">
                            Feedback
                        </label>

                        <textarea
                            className="form-control"
                            rows="3"
                            name="feedback"
                            value={form.feedback}
                            onChange={handleChange}
                            required
                        />

                    </div>

                    <div className="col-md-6 mb-3">

                        <label className="form-label">
                            Reviewer
                        </label>

                        <input
                            className="form-control"
                            name="reviewer"
                            value={form.reviewer}
                            onChange={handleChange}
                            required
                        />

                    </div>

                    <div className="col-md-6 mb-3">

                        <label className="form-label">
                            Review Date
                        </label>

                        <input
                            type="date"
                            className="form-control"
                            name="reviewDate"
                            value={form.reviewDate}
                            onChange={handleChange}
                            required
                        />

                    </div>

                </div>

                <button
                    className="btn btn-success me-2"
                    type="submit"
                >
                    {editingId ? "Update Review" : "Save Review"}
                </button>

                {editingId && (

                    <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={cancelEdit}
                    >
                        Cancel
                    </button>

                )}

            </form>

        </div>

    </div>
    )}
    <div className="card shadow">

        <div className="card-header bg-dark text-white">

            <h4 className="mb-0">
                Performance Reviews
            </h4>

        </div>

        <div className="card-body">

            {loading ? (

                <p>Loading...</p>

            ) : (

                <table className="table table-bordered table-hover">

                    <thead className="table-light">

                        <tr>

                            <th>Employee</th>
                            <th>Department</th>
                            <th>Rating</th>
                            <th>Reviewer</th>
                            <th>Review Date</th>
                            {isAdmin && (
    <th width="180">Actions</th>
)}

                        </tr>

                    </thead>

                    <tbody>

                        {reviews.length === 0 ? (

                            <tr>

                                <td
                                    colSpan="6"
                                    className="text-center"
                                >
                                    No Performance Reviews
                                </td>

                            </tr>

                        ) : (

                            filteredReviews.map((review) => (

                                <tr key={review.id}>

                                    <td>{review.employeeName}</td>
                                    <td>{review.department}</td>
                                    <td>{review.rating}</td>
                                    <td>{review.reviewer}</td>
                                    <td>{review.reviewDate}</td>

                                   {isAdmin && (

<td>

    <button
        className="btn btn-warning btn-sm me-2"
        onClick={() => editReview(review)}
    >
        Edit
    </button>

    <button
        className="btn btn-danger btn-sm"
        onClick={() => deleteReview(review.id)}
    >
        Delete
    </button>

</td>

)}
                                </tr>

                            ))

                        )}

                    </tbody>

                </table>

            )}

        </div>

    </div>

</div>

);
}