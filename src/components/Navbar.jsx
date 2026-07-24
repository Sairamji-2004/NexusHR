import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useEffect, useState } from "react";
import { NOTIFICATION_API } from "../api/api";
import NotificationBell from "./NotificationBell";

export default function Navbar() {

    const {
        isAuthenticated,
        fullName,
        roles,
        logout
    } = useAuth();

    const navigate = useNavigate();

    const isAdmin =
        roles.includes("ROLE_HR_ADMIN") ||
        roles.includes("ROLE_SUPER_ADMIN");

    const [unreadCount, setUnreadCount] = useState(0);

    useEffect(() => {

        if (!isAuthenticated) return;

        loadUnreadCount();

        const interval = setInterval(() => {
            loadUnreadCount();
        }, 3000);

        return () => clearInterval(interval);

    }, [isAuthenticated]);

    async function loadUnreadCount() {

        try {

            const token = localStorage.getItem("token");
            const employeeId = localStorage.getItem("employeeId");

            if (!employeeId) {
                setUnreadCount(0);
                return;
            }

            const response = await fetch(
                `${NOTIFICATION_API}/employee/${employeeId}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            if (!response.ok) {
                setUnreadCount(0);
                return;
            }

            const data = await response.json();

            if (!Array.isArray(data)) {
                setUnreadCount(0);
                return;
            }

            const unread = data.filter(
                notification => !notification.isRead
            ).length;

            setUnreadCount(unread);

        } catch (err) {
            console.error(err);
        }
    }

    async function handleLogout() {

        try {
            await logout();
        } catch (err) {
            console.error(err);
        } finally {
            navigate("/login");
        }
    }

    return (

        <nav className="navbar">

            <div className="navbar-brand">
                NexusHR
            </div>

            {isAuthenticated && (

                <div className="navbar-links">

                    <Link to="/dashboard">
                        Dashboard
                    </Link>

                    <Link to="/attendance">
                        Attendance
                    </Link>

                    <Link to="/leave">
                        Leave
                    </Link>

                    <Link to="/performance">
                        Performance
                    </Link>

                    {isAdmin && (
                        <>
                            <Link to="/employees">
                                Employees
                            </Link>

                            <Link to="/payroll">
                                Payroll
                            </Link>

                            <Link to="/payroll/generate">
                                Generate Payroll
                            </Link>

                            <Link to="/analytics">
                                Analytics
                            </Link>

                            <Link to="/reports">
                                Reports
                            </Link>
                        </>
                    )}

                    <NotificationBell />

                    <span className="navbar-user">
                        {fullName}
                    </span>

                    <button onClick={handleLogout}>
                        Logout
                    </button>

                </div>

            )}

        </nav>

    );
}