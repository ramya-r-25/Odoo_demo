# -*- coding: utf-8 -*-
{
    'name': 'Dayflow Employee Profile',
    'version': '1.0',
    'category': 'Human Resources',
    'summary': 'Employee Profile Management Module for Dayflow HRMS',
    'description': """
        Dayflow HRMS - Employee Profile Module
        ======================================
        Manages employee profiles including basic details, job position, department,
        salary structure, profile picture, and documents.
    """,
    'author': 'Dayflow Team',
    'depends': ['base'],
    'data': [
        'security/security_groups.xml',
        'security/ir.model.access.csv',
        'views/employee_views.xml',
    ],
    'installable': True,
    'application': True,
    'auto_install': False,
}
