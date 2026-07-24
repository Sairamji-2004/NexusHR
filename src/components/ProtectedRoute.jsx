import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({

    children,

    allowedRoles = []

}) {

    const {

        isAuthenticated,

        roles

    } = useAuth();

    if (!isAuthenticated) {

        return <Navigate to="/login" replace />;

    }

    if (

        allowedRoles.length > 0 &&

        !roles.some(role => allowedRoles.includes(role))

    ) {

        return <Navigate to="/dashboard" replace />;

    }

    return children;

}