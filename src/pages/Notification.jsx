import { useEffect, useState } from "react";
import { NOTIFICATION_API } from "../api/api";
import { useAuth } from "../context/AuthContext";
export default function Notification() {
      const { employeeId } = useAuth();
    const [notifications, setNotifications] = useState([]);
    const [filter, setFilter] = useState("ALL");
    const [search, setSearch] = useState("");

    useEffect(() => {

        loadNotifications();

        const interval = setInterval(() => {
            loadNotifications();
        }, 5000);

        return () => clearInterval(interval);

    }, []);

   async function loadNotifications() {

    try {

        const token = localStorage.getItem("token");

        console.log("Employee ID:", employeeId);

        if (!employeeId) {
            console.error("Employee ID not found in localStorage");
            return;
        }

        const response = await fetch(
            `${NOTIFICATION_API}`,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
   console.log("Status:", response.status);

        if (!response.ok) {
            throw new Error("Failed to load notifications");
        }

        const data = await response.json();
  console.log("Notifications:", data);  
        data.sort(
            (a, b) =>
                new Date(b.createdAt) -
                new Date(a.createdAt)
        );

        setNotifications(data);

    } catch (err) {
        console.error(err);
    }
}
    async function markAsRead(id) {

        try {

            const token = localStorage.getItem("token");

            await fetch(`${NOTIFICATION_API}/${id}/read`, {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            loadNotifications();

        } catch (err) {
            console.error(err);
        }
    }

    async function deleteNotification(id) {

        try {

            const token = localStorage.getItem("token");

            if (!window.confirm("Delete this notification?")) return;

            await fetch(`${NOTIFICATION_API}/${id}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            loadNotifications();

        } catch (err) {
            console.error(err);
        }
    }

    const totalCount = notifications.length;

    const unreadCount = notifications.filter(
        n => !n.isRead
    ).length;

    const readCount = notifications.filter(
        n => n.isRead
    ).length;

    const infoCount = notifications.filter(
        n => n.type === "INFO"
    ).length;

    const warningCount = notifications.filter(
        n => n.type === "WARNING"
    ).length;

    const errorCount = notifications.filter(
        n => n.type === "ERROR"
    ).length;

  const filteredNotifications = notifications.filter((notification) => {

    const matchesFilter =
        filter === "ALL"
            ? true
            : filter === "READ"
                ? notification.isRead
                : !notification.isRead;

    const searchText = search.trim().toLowerCase();

    const matchesSearch =
        searchText === "" ||
        (notification.title ?? "")
            .toLowerCase()
            .includes(searchText) ||
        (notification.message ?? "")
            .toLowerCase()
            .includes(searchText);

    return matchesFilter && matchesSearch;

});

    return (

        <div className="container mt-4">

            <h2 className="mb-4">
                Notifications
                <span className="badge bg-danger ms-2">
                    {unreadCount}
                </span>
            </h2>

            <div className="row mb-4">

                <div className="col-md-2">
                    <div className="card bg-primary text-white text-center">
                        <div className="card-body">
                            <h3>{totalCount}</h3>
                            <small>Total</small>
                        </div>
                    </div>
                </div>

                <div className="col-md-2">
                    <div className="card bg-danger text-white text-center">
                        <div className="card-body">
                            <h3>{unreadCount}</h3>
                            <small>Unread</small>
                        </div>
                    </div>
                </div>

                <div className="col-md-2">
                    <div className="card bg-success text-white text-center">
                        <div className="card-body">
                            <h3>{readCount}</h3>
                            <small>Read</small>
                        </div>
                    </div>
                </div>

                <div className="col-md-2">
                    <div className="card bg-info text-white text-center">
                        <div className="card-body">
                            <h3>{infoCount}</h3>
                            <small>INFO</small>
                        </div>
                    </div>
                </div>

                <div className="col-md-2">
                    <div className="card bg-warning text-dark text-center">
                        <div className="card-body">
                            <h3>{warningCount}</h3>
                            <small>WARNING</small>
                        </div>
                    </div>
                </div>

                <div className="col-md-2">
                    <div className="card bg-dark text-white text-center">
                        <div className="card-body">
                            <h3>{errorCount}</h3>
                            <small>ERROR</small>
                        </div>
                    </div>
                </div>

            </div>

            <div className="d-flex justify-content-between mb-3">

                <input
                    className="form-control"
                    style={{ width: "300px" }}
                    placeholder="Search notifications..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />

                <div>

                    <button
                        className={`btn me-2 ${
                            filter === "ALL"
                                ? "btn-primary"
                                : "btn-outline-primary"
                        }`}
                        onClick={() => setFilter("ALL")}
                    >
                        All
                    </button>

                    <button
                        className={`btn me-2 ${
                            filter === "UNREAD"
                                ? "btn-danger"
                                : "btn-outline-danger"
                        }`}
                        onClick={() => setFilter("UNREAD")}
                    >
                        Unread
                    </button>

                    <button
                        className={`btn ${
                            filter === "READ"
                                ? "btn-success"
                                : "btn-outline-success"
                        }`}
                        onClick={() => setFilter("READ")}
                    >
                        Read
                    </button>

                </div>

            </div>

            <table className="table table-bordered table-hover">

                <thead className="table-dark">

                    <tr>
                        <th>No</th>
                        <th>Title</th>
                        <th>Message</th>
                        <th>Type</th>
                        <th>Status</th>
                        <th>Created</th>
                        <th>Actions</th>
                    </tr>

                </thead>

                <tbody>

                    {filteredNotifications.length === 0 ? (

                        <tr>
                            <td colSpan="7" className="text-center">
                                No notifications found.
                            </td>
                        </tr>

                    ) : (
filteredNotifications.map((notification, index) => {
    console.log("Rendering:", index, notification.title);

    return (
        <tr key={notification.id}>
            <td>{index + 1}</td>
            <td>{notification.title}</td>
            <td>{notification.message}</td>

            <td>
                <span
                    className={`badge ${
                        notification.type === "INFO"
                            ? "bg-primary"
                            : notification.type === "WARNING"
                            ? "bg-warning text-dark"
                            : notification.type === "ERROR"
                            ? "bg-danger"
                            : "bg-secondary"
                    }`}
                >
                    {notification.type}
                </span>
            </td>

            <td>
                {notification.isRead ? (
                    <span className="badge bg-success">
                        Read
                    </span>
                ) : (
                    <span className="badge bg-danger">
                        Unread
                    </span>
                )}
            </td>

            <td>
                {new Date(notification.createdAt).toLocaleString()}
            </td>

            <td>
                {!notification.isRead && (
                    <button
                        className="btn btn-success btn-sm me-2"
                        onClick={() => markAsRead(notification.id)}
                    >
                        Mark Read
                    </button>
                )}

                <button
                    className="btn btn-danger btn-sm"
                    onClick={() => deleteNotification(notification.id)}
                >
                    Delete
                </button>
            </td>
        </tr>
    );
})
                      

                    )}

                </tbody>

            </table>

        </div>

    );

}