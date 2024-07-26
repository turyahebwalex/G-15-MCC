<!DOCTYPE html>
<html>
<head>
    <title>Reject Applicant</title>
    <meta name="csrf-token" content="{{ csrf_token() }}">
</head>
<body>
    <h1>Reject Applicant</h1>
    <form action="{{ url('/reject') }}" method="POST">
        @csrf
        <input type="hidden" name="applicant_id" value="{{ $applicant->id }}">
        <button type="submit">Reject Applicant</button>
    </form>
</body>
</html>
