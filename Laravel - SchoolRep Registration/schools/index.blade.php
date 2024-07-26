@extends('layouts.app1')

@section('title', 'Schools')

@section('content')
    <div class="container mt-5">
        <h1 class="text-pink mb-4">Schools</h1>
        <a href="{{ route('schools.create') }}" class="btn btn-primary mb-3">Add New School</a>

        @if(session('success'))
            <div class="alert alert-success">{{ session('success') }}</div>
        @endif

        <table class="table table-striped table-bordered">
            <thead class="table-primary">
                <tr>
                    <th>Registration Number</th>
                    <th>Name</th>
                    <th>District</th>
                    <th>Representative Name</th>
                    <th>Representative Email</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                @foreach($schools as $school)
                    <tr>
                        <td>{{ $school->registrationNumber }}</td>
                        <td>{{ $school->name }}</td>
                        <td>{{ $school->district }}</td>
                        <td>{{ $school->representativeName }}</td>
                        <td>{{ $school->representativeEmail }}</td>
                        <td>
                            <a href="{{ route('schools.show', $school->registrationNumber) }}" class="btn btn-info btn-sm">View</a>
                            <a href="{{ route('schools.edit', $school->registrationNumber) }}" class="btn btn-warning btn-sm">Edit</a>
                            <form action="{{ route('schools.destroy', $school->registrationNumber) }}" method="POST" style="display: inline;">
                                @csrf
                                @method('DELETE')
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                            </form>
                        </td>
                    </tr>
                @endforeach
            </tbody>
        </table>
    </div>

    @push('styles')
    <style>
        .text-pink {
            color: #e91e63; 
        }
        .table-primary {
            background-color: #f8f9fa; 
        }
        .table-bordered th, .table-bordered td {
            border: 1px solid #dee2e6; /* Light grey border for table cells */
        }
        .table-striped tbody tr:nth-of-type(odd) {
            background-color: #f2f2f2; /* Zebra striping for table rows */
        }
        .table-hover tbody tr:hover {
            background-color: #e91e63; /* Hover effect for rows */
        }
    </style>
    @endpush
@endsection
