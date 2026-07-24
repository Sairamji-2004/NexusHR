import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Navbar from "./components/Navbar";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Employees from "./pages/Employees";
import Attendance from "./pages/Attendance";
import Payroll from "./pages/Payroll";
import PayrollGenerate from "./pages/PayrollGenerate";
import Leave from "./pages/Leave";
import ApplyLeave from "./pages/ApplyLeave";
import Performance from "./pages/Performance";
import Notification from "./pages/Notification";
import Analytics from "./pages/Analytics";
import Reports from "./pages/Reports";

import "./styles.css";

export default function App() {

    return (

        <AuthProvider>

            <BrowserRouter>

                <Navbar />

                <Routes>

                    <Route
                        path="/login"
                        element={<Login />}
                    />

                    {/* Dashboard */}

                    <Route
                        path="/dashboard"
                        element={
                            <ProtectedRoute>
                                <Dashboard />
                            </ProtectedRoute>
                        }
                    />

                    {/* Employees (Admin Only) */}

                    <Route
                        path="/employees"
                        element={
                            <ProtectedRoute
                                allowedRoles={[
                                    "ROLE_HR_ADMIN",
                                    "ROLE_SUPER_ADMIN"
                                ]}
                            >
                                <Employees />
                            </ProtectedRoute>
                        }
                    />

                    {/* Attendance */}

                    <Route
                        path="/attendance"
                        element={
                            <ProtectedRoute>
                                <Attendance />
                            </ProtectedRoute>
                        }
                    />

                    {/* Leave */}

                    <Route
                        path="/leave"
                        element={
                            <ProtectedRoute>
                                <Leave />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/apply-leave"
                        element={
                            <ProtectedRoute>
                                <ApplyLeave />
                            </ProtectedRoute>
                        }
                    />

                    {/* Payroll (Admin Only) */}

                    <Route
                        path="/payroll"
                        element={
                            <ProtectedRoute
                                allowedRoles={[
                                    "ROLE_HR_ADMIN",
                                    "ROLE_SUPER_ADMIN"
                                ]}
                            >
                                <Payroll />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/payroll/generate"
                        element={
                            <ProtectedRoute
                                allowedRoles={[
                                    "ROLE_HR_ADMIN",
                                    "ROLE_SUPER_ADMIN"
                                ]}
                            >
                                <PayrollGenerate />
                            </ProtectedRoute>
                        }
                    />

                    {/* Performance */}

                    <Route
                        path="/performance"
                        element={
                            <ProtectedRoute>
                                <Performance />
                            </ProtectedRoute>
                        }
                    />

                    {/* Notifications */}

                    <Route
                        path="/notifications"
                        element={
                            <ProtectedRoute>
                                <Notification />
                            </ProtectedRoute>
                        }
                    />

                    {/* Analytics (Admin Only) */}

                    <Route
                        path="/analytics"
                        element={
                            <ProtectedRoute
                                allowedRoles={[
                                    "ROLE_HR_ADMIN",
                                    "ROLE_SUPER_ADMIN"
                                ]}
                            >
                                <Analytics />
                            </ProtectedRoute>
                        }
                    />

                    {/* Reports (Admin Only) */}

                    <Route
                        path="/reports"
                        element={
                            <ProtectedRoute
                                allowedRoles={[
                                    "ROLE_HR_ADMIN",
                                    "ROLE_SUPER_ADMIN"
                                ]}
                            >
                                <Reports />
                            </ProtectedRoute>
                        }
                    />

                    {/* Default */}

                    <Route
                        path="*"
                        element={
                            <Navigate
                                to="/dashboard"
                                replace
                            />
                        }
                    />

                </Routes>

            </BrowserRouter>

        </AuthProvider>

    );

}