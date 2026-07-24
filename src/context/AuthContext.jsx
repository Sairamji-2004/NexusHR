import { createContext, useContext, useState } from "react";
import { AUTH_API, apiPost } from "../api/api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {

    const [token, setToken] = useState(
        localStorage.getItem("token")
    );

    const [employeeId, setEmployeeId] = useState(
        localStorage.getItem("employeeId")
    );

    const [fullName, setFullName] = useState(
        localStorage.getItem("fullName")
    );

    const [roles, setRoles] = useState(() => {

        const stored =
            localStorage.getItem("roles");

        return stored
            ? JSON.parse(stored)
            : [];

    });


    const [tenantId, setTenantId] = useState(
        localStorage.getItem("tenantId")
    );


    async function login(email, password) {

        const response = await apiPost(
            `${AUTH_API}/login`,
            {
                email,
                password
            }
        );


        const payload = response.data;


        if (!payload || !payload.accessToken) {

            throw new Error(
                "No access token returned from Auth service"
            );

        }


        const accessToken =
            payload.accessToken;


        const empId =
            payload.employeeId || "";


        const name =
            payload.fullName || "";


        const userRoles =
            payload.roles || [];


        const userTenant =
            payload.tenantId || "";



        // Save Local Storage

        localStorage.setItem(
            "token",
            accessToken
        );

        localStorage.setItem(
            "employeeId",
            empId
        );

        localStorage.setItem(
            "fullName",
            name
        );

        localStorage.setItem(
            "roles",
            JSON.stringify(userRoles)
        );

        localStorage.setItem(
            "tenantId",
            userTenant
        );



        // Update State

        setToken(accessToken);

        setEmployeeId(empId);

        setFullName(name);

        setRoles(userRoles);

        setTenantId(userTenant);

    }



    async function logout() {

        try {

            await apiPost(
                `${AUTH_API}/logout`,
                {}
            );


        } catch(error) {

            console.log(
                "Logout API failed:",
                error.message
            );


        } finally {


            localStorage.removeItem("token");

            localStorage.removeItem("employeeId");

            localStorage.removeItem("fullName");

            localStorage.removeItem("roles");

            localStorage.removeItem("tenantId");



            setToken(null);

            setEmployeeId(null);

            setFullName(null);

            setRoles([]);

            setTenantId(null);

        }

    }



    return (

        <AuthContext.Provider

            value={{

                token,

                employeeId,

                fullName,

                roles,

                tenantId,


                login,

                logout,


                isAuthenticated:
                    Boolean(token)

            }}

        >

            {children}

        </AuthContext.Provider>

    );

}



export function useAuth() {

    return useContext(AuthContext);

}