// ===============================
// API Base URLs
// ===============================

export const AUTH_API = "http://localhost:8080/api/v1/auth";
export const EMPLOYEE_API = "http://localhost:8082/api/v1/employees";
export const ATTENDANCE_API = "http://localhost:8086/api/attendance";
export const PAYROLL_API = "http://localhost:8084/api/payroll";
export const LEAVE_API = "http://localhost:8087/api/leaves";
export const PERFORMANCE_API = "http://localhost:8085/api/performance";
export const NOTIFICATION_API = "http://localhost:8088/api/notifications";
// ===============================
// JWT Headers
// ===============================

function authHeaders() {

    const token = localStorage.getItem("token");

    console.log("TOKEN =", token);

    return {
        "Content-Type": "application/json",
        ...(token && {
            Authorization: `Bearer ${token}`
        })
    };
}



// ===============================
// Response Handler
// ===============================

async function handleResponse(response) {

    const contentType =
        response.headers.get("content-type") || "";


    const data = contentType.includes("application/json")
        ? await response.json()
        : await response.text();


    if (response.status === 401) {

        localStorage.removeItem("token");

        window.location.href = "/login";

        throw new Error("Unauthorized");
    }


    if (!response.ok) {

        throw new Error(
            data.message || 
            `Request failed ${response.status}`
        );
    }


    return data;
}



// ===============================
// GET
// ===============================

export async function apiGet(url) {

    const response = await fetch(url, {

        method:"GET",

        headers:authHeaders()

    });


    return handleResponse(response);
}



// ===============================
// POST
// ===============================

export async function apiPost(url,data) {

    const response = await fetch(url,{

        method:"POST",

        headers:authHeaders(),

        body:JSON.stringify(data)

    });


    return handleResponse(response);
}



// ===============================
// PUT
// ===============================

export async function apiPut(url,data){

    const response = await fetch(url,{

        method:"PUT",

        headers:authHeaders(),

        body:JSON.stringify(data)

    });


    return handleResponse(response);
}



// ===============================
// PATCH
// ===============================

export async function apiPatch(url,data=null){

    const response = await fetch(url,{

        method:"PATCH",

        headers:authHeaders(),

        ...(data && {
            body:JSON.stringify(data)
        })

    });


    return handleResponse(response);

}



// ===============================
// DELETE
// ===============================

export async function apiDelete(url){

    const response = await fetch(url,{

        method:"DELETE",

        headers:authHeaders()

    });


    return handleResponse(response);

}



// ===============================
// Download Payslip
// ===============================

export async function downloadPayslip(id){

    const response = await fetch(
        `${PAYROLL_API}/${id}/payslip`,
        {
            headers:authHeaders()
        }
    );


    if(!response.ok){

        throw new Error(
            "Payslip download failed"
        );
    }


    const blob = await response.blob();


    const url =
        window.URL.createObjectURL(blob);


    const a=document.createElement("a");

    a.href=url;

    a.download=`Payslip-${id}.pdf`;

    document.body.appendChild(a);

    a.click();

    a.remove();

    window.URL.revokeObjectURL(url);
}



// ===============================
// Export Employees
// ===============================

export async function exportEmployeesExcel(){

    const response = await fetch(
        `${EMPLOYEE_API}/export`,
        {
            headers:authHeaders()
        }
    );


    if(!response.ok){

        throw new Error(
            "Employee export failed"
        );

    }


    const blob =
        await response.blob();


    const url =
        window.URL.createObjectURL(blob);


    const a=document.createElement("a");

    a.href=url;

    a.download="Employees.xlsx";


    document.body.appendChild(a);

    a.click();

    a.remove();

}



// ===============================
// Export Payroll
// ===============================

export async function exportPayrollExcel(){

    const response = await fetch(
        `${PAYROLL_API}/export`,
        {
            headers:authHeaders()
        }
    );


    if(!response.ok){

        throw new Error(
            "Payroll export failed"
        );
    }


    const blob =
        await response.blob();


    const url =
        window.URL.createObjectURL(blob);


    const a=document.createElement("a");


    a.href=url;

    a.download="Payroll.xlsx";


    document.body.appendChild(a);

    a.click();


    a.remove();

}
// ===============================
// Leave Approval
// ===============================

export async function approveLeave(id) {

    const response = await fetch(
        `${LEAVE_API}/${id}/approve`,
        {
            method: "PUT",
            headers: authHeaders()
        }
    );

    return handleResponse(response);
}

export async function rejectLeave(id) {

    const response = await fetch(
        `${LEAVE_API}/${id}/reject`,
        {
            method: "PUT",
            headers: authHeaders()
        }
    );

    return handleResponse(response);
}
export async function deleteLeave(id) {

    return apiDelete(`${LEAVE_API}/${id}`);

}